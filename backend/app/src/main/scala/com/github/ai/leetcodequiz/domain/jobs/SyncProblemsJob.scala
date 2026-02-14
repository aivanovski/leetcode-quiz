package com.github.ai.leetcodequiz.domain.jobs

import com.github.ai.leetcodequiz.data.db.model.{DataSyncEntity, ProblemId, SyncType, SyncUid}
import com.github.ai.leetcodequiz.data.db.repository.{DataSyncRepository, ProblemRepository}
import com.github.ai.leetcodequiz.data.file.FileSystemProvider
import com.github.ai.leetcodequiz.data.json.StreamingProblemParser
import com.github.ai.leetcodequiz.domain.usecases.{
  CloneGithubRepositoryUseCase,
  DownloadFileUseCase
}
import com.github.ai.leetcodequiz.entity.{Problem, RelativePath}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

import java.time.{Duration, LocalDateTime, ZoneOffset}

class SyncProblemsJob(
  private val fileSystemProvider: FileSystemProvider,
  private val syncRepository: DataSyncRepository,
  private val problemRepository: ProblemRepository,
  private val problemParser: StreamingProblemParser,
  private val cloneRepositoryUseCase: CloneGithubRepositoryUseCase,
  private val downloadFileUseCase: DownloadFileUseCase
) extends ScheduledJob(
      syncRepository = syncRepository,
      interval = 12.hours,
      syncType = SyncType.PROBLEMS
    ) {

  override def run(): IO[DomainError, Unit] = defer {
    val syncUid = onSyncStart().run

    val lastSync = syncRepository.getLatestSync(SyncType.PROBLEMS).run
    val destinationDirPath = RelativePath(s"problems/${syncUid.toString}")
    val destinationFile = RelativePath(destinationDirPath.relativePath + "/result.json")

    ZIO
      .logInfo(
        "Last problems sync happened: %s; last sync time: %s".format(
          formatLastSyncTimeDifference(lastSync),
          lastSync.map(_.timestamp)
        )
      )
      .run

    downloadFileUseCase
      .downloadFile(
        url = "https://raw.githubusercontent.com/doocs/leetcode/main/solution/result.json",
        destinationDir = destinationDirPath,
        destinationFile = destinationFile
      )
      .run

    val remoteProblems = ZIO.scoped {
      fileSystemProvider
        .inputStream(destinationFile)
        .flatMap(stream =>
          problemParser
            .parseStream(stream)
            .runCollect
            .map(_.toList)
        )
    }.run
    val localProblems = problemRepository.getAll().run

    syncProblemsWithDatabase(
      remoteProblems = remoteProblems,
      localProblems = localProblems
    ).run

    syncComplete(syncUid).run

    ZIO.logInfo(s"Removing files in: ${destinationDirPath.relativePath}").run
    fileSystemProvider.remove(destinationDirPath).run

    ZIO.logInfo("Sync problems job finished.").run

    ()
  }

  private def syncProblemsWithDatabase(
    remoteProblems: List[Problem],
    localProblems: List[Problem]
  ): IO[DomainError, Unit] = defer {
    val remoteProblemsMap =
      remoteProblems.map(problem => problem.id.asInstanceOf[Long] -> problem).toMap
    val localProblemsMap =
      localProblems.map(problem => problem.id.asInstanceOf[Long] -> problem).toMap

    val remoteIds = remoteProblemsMap.keySet
    val localIds = localProblemsMap.keySet

    // Find insertions (in remote but not in local)
    val insertions = remoteIds.diff(localIds).toList.sorted

    // Find deletions (in local but not in remote)
    val deletions = localIds.diff(remoteIds).toList.sorted

    // Find potential updates (in both remote and local)
    val potentialUpdates = remoteIds.intersect(localIds).toList.sorted
    val updates = potentialUpdates.filter { id =>
      val remote = remoteProblemsMap(id)
      val local = localProblemsMap(id)
      !problemsAreEqual(remote, local)
    }

    ZIO
      .logInfo(
        s"Problem sync summary: ${insertions.size} insertions, ${updates.size} updates, ${deletions.size} deletions"
      )
      .run

    // Perform insertions
    if (insertions.nonEmpty) {
      val startTime = Clock.nanoTime.run

      ZIO.logInfo(s"Inserting ${insertions.size} new problems:").run
      ZIO
        .foreach(insertions) { id =>
          val problem = remoteProblemsMap(id)
          ZIO.logInfo(s"  + [$id] ${problem.title}") *>
            problemRepository.add(problem)
        }
        .run

      val endTime = Clock.nanoTime.run
      val elapsedTime = (endTime - startTime) / 1000000L
      ZIO.logInfo(s"Inserting of ${insertions.size} took $elapsedTime ms").run
    }

    // Perform updates
    if (updates.nonEmpty) {
      ZIO.logInfo(s"Updating ${updates.size} problems:").run
      ZIO
        .foreach(updates) { id =>
          val problem = remoteProblemsMap(id)
          ZIO.logInfo(s"  ~ [$id] ${problem.title}") *>
            problemRepository.update(problem)
        }
        .run
    }

    // Perform deletions
    if (deletions.nonEmpty) {
      ZIO.logInfo(s"Deleting ${deletions.size} problems:").run
      ZIO
        .foreach(deletions) { id =>
          val problem = localProblemsMap(id)
          ZIO.logInfo(s"  - [$id] ${problem.title}") *>
            problemRepository.delete(ProblemId(id))
        }
        .run
    }

    if (insertions.isEmpty && updates.isEmpty && deletions.isEmpty) {
      ZIO.logInfo("No problem changes detected. Database is up to date.").run
    }

    ()
  }

  private def problemsAreEqual(lhs: Problem, rhs: Problem): Boolean = {
    lhs.title == rhs.title &&
    lhs.content == rhs.content &&
    lhs.category == rhs.category &&
    lhs.url == rhs.url &&
    lhs.difficulty == rhs.difficulty &&
    lhs.hints == rhs.hints &&
    lhs.likes == rhs.likes &&
    lhs.dislikes == rhs.dislikes
  }

  private def formatLastSyncTimeDifference(lastSync: Option[DataSyncEntity]): String = {
    val currentTime = LocalDateTime.now(ZoneOffset.UTC)

    val timeDifference = Duration.between(
      lastSync.map(_.timestamp).getOrElse(currentTime),
      currentTime
    )

    val days = timeDifference.toDays
    val hours = timeDifference.toHoursPart
    val minutes = timeDifference.toMinutesPart
    val seconds = timeDifference.toSecondsPart

    if (days != 0 || hours != 0 || minutes != 0 || seconds != 0) {
      "%d days, %d hours, %d minutes, %d seconds ago".format(days, hours, minutes, seconds)
    } else {
      "-"
    }
  }
}
