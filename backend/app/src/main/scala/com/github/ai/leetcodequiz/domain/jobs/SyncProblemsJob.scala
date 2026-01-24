package com.github.ai.leetcodequiz.domain.jobs

import com.github.ai.leetcodequiz.data.db.model.{DataSyncEntity, ProblemId, SyncType, SyncUid}
import com.github.ai.leetcodequiz.data.db.repository.{DataSyncRepository, ProblemRepository}
import com.github.ai.leetcodequiz.data.file.FileSystemProvider
import com.github.ai.leetcodequiz.data.json.ProblemParser
import com.github.ai.leetcodequiz.domain.usecases.CloneGithubRepositoryUseCase
import com.github.ai.leetcodequiz.entity.{Problem, RelativePath}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

import java.time.{Duration, LocalDateTime, ZoneOffset}
import java.util.UUID

class SyncProblemsJob(
  private val fileSystemProvider: FileSystemProvider,
  private val problemSyncRepository: DataSyncRepository,
  private val problemRepository: ProblemRepository,
  private val problemParser: ProblemParser,
  private val cloneRepositoryUseCase: CloneGithubRepositoryUseCase
) extends ScheduledJob {

  override val interval: Duration = 12.hours

  override def run(): IO[DomainError, Unit] = defer {
    ZIO.logInfo("Start sync problems job.").run

    val syncUid = UUID.randomUUID()
    val lastSync = problemSyncRepository.getLatestSync(SyncType.PROBLEMS).run
    val destinationDirPath = RelativePath(s"github/${syncUid.toString}")

    val timeThreshold = LocalDateTime.now(ZoneOffset.UTC).minusHours(2)
    val shouldSync = lastSync.isEmpty || lastSync.get.timestamp.isBefore(timeThreshold)

    ZIO
      .logInfo(
        "Last problems sync happened: %s; last sync time: %s, should do sync: %s".format(
          formatLastSyncTimeDifference(lastSync),
          lastSync.map(_.timestamp),
          shouldSync
        )
      )
      .run

    if (shouldSync) {
      val repoDir = cloneRepositoryUseCase
        .cloneRepository(
          repositoryUrl = "https://github.com/aivanovski/leetcode-api.git",
          destinationDirPath = destinationDirPath
        )
        .run

      val problemsFile = RelativePath(s"${repoDir.path}/data/leetcode_questions.json")
      val content = fileSystemProvider.readContent(problemsFile).run

      val remoteProblems = problemParser.parse(content).run
      val localProblems = problemRepository.getAll().run

      syncProblemsWithDatabase(
        remoteProblems = remoteProblems,
        localProblems = localProblems
      ).run

      problemSyncRepository
        .add(
          DataSyncEntity(
            uid = SyncUid(syncUid),
            syncType = SyncType.PROBLEMS,
            timestamp = LocalDateTime.now(ZoneOffset.UTC)
          )
        )
        .run

      ZIO.logInfo(s"Removing files in: ${destinationDirPath.relativePath}").run
      fileSystemProvider.remove(destinationDirPath).run
    }

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
