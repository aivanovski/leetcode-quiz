package com.github.ai.leetcodequiz.domain.jobs

import com.github.ai.leetcodequiz.data.db.model.{
  ProblemId,
  SourceType,
  SolutionEntity,
  SolutionUid,
  SyncType,
  SyncUid
}
import com.github.ai.leetcodequiz.data.db.repository.{
  DataSyncRepository,
  ProblemRepository,
  SolutionRepository
}
import com.github.ai.leetcodequiz.data.file.FileSystemProvider
import com.github.ai.leetcodequiz.domain.usecases.CloneGithubRepositoryUseCase
import com.github.ai.leetcodequiz.entity.RelativePath
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

import java.util.UUID

class SyncExternalSolutionsJob(
  private val cloneRepositoryUseCase: CloneGithubRepositoryUseCase,
  private val fileSystemProvider: FileSystemProvider,
  private val syncRepository: DataSyncRepository,
  private val solutionRepository: SolutionRepository,
  private val problemRepository: ProblemRepository
) extends ScheduledJob(
      syncRepository = syncRepository,
      interval = 6.hours,
      syncType = SyncType.EXTERNAL_SOLUTIONS,
      dependsOn = List(SyncType.PROBLEMS)
    ) {

  override def run(): IO[DomainError, Unit] = defer {
    val syncUid = onSyncStart().run
    val destinationDirPath = RelativePath(s"github/external/${syncUid.toString}")

    val repoDir = cloneRepositoryUseCase
      .cloneRepository(
        repositoryUrl = "https://github.com/neetcode-gh/leetcode.git",
        destinationDirPath = destinationDirPath
      )
      .run

    val files = fileSystemProvider
      .listFileTree(repoDir, maxDepth = 16)
      .run
      .flatten

    val filesAndProblemIds = files.flatMap { file =>
      parseProblemId(file.path).map(id => (file, ProblemId(id)))
    }
    val knownProblemIds = problemRepository.getAll().run.map(_.id).toSet
    val (knownFilesAndProblemIds, unknownFilesAndProblemIds) =
      filesAndProblemIds.partition { case (_, problemId) => knownProblemIds.contains(problemId) }

    if (unknownFilesAndProblemIds.nonEmpty) {
      val unknownIds = unknownFilesAndProblemIds.map(_._2).distinct.sorted
      ZIO
        .logWarning(
          s"Skipping ${unknownFilesAndProblemIds.size} external solutions with unknown problem ids: ${unknownIds.mkString(", ")}"
        )
        .run
    }

    val localSolutions = solutionRepository.findBySourceType(SourceType.EXTERNAL).run
    val remoteSolutions = ZIO.collectAll {
      knownFilesAndProblemIds.map { (file, problemId) =>
        fileSystemProvider
          .readContent(file)
          .map { content =>
            SolutionItem(
              problemId = problemId,
              content = content,
              path = repoDir.relativize(file).toString
            )
          }
      }
    }.run

    syncSolutionsWithDatabase(
      remoteSolutions = remoteSolutions,
      localSolutions = localSolutions
    ).run

    syncComplete(syncUid).run

    ZIO.logInfo(s"Removing files in: ${destinationDirPath.relativePath}").run
    fileSystemProvider.remove(destinationDirPath).run

    ()
  }

  private def parseProblemId(path: String): Option[Int] = {
    if (!path.endsWith(".kt")) return None

    val segments = path.split("[/\\\\]").toList.reverse
    val pattern = "(?<!\\d)(\\d{1,6})(?!\\d)".r

    segments
      .flatMap(segment => pattern.findFirstMatchIn(segment).map(_.group(1)))
      .headOption
      .flatMap(_.toIntOption)
  }

  private def syncSolutionsWithDatabase(
    remoteSolutions: List[SolutionItem],
    localSolutions: List[SolutionEntity]
  ): IO[DomainError, Unit] = defer {
    val remoteSolutionMap = remoteSolutions.groupBy(s => s.problemId)
    val localSolutionMap = localSolutions.groupBy(s => s.problemId)

    val remoteIds = remoteSolutionMap.keySet
    val localIds = localSolutionMap.keySet

    val insertions = remoteIds.diff(localIds).toList.sorted
    val deletions = localIds.diff(remoteIds).toList.sorted
    val potentialUpdates = remoteIds.intersect(localIds).toList.sorted

    val problemIdToNameMap = problemRepository
      .getAll()
      .run
      .map(p => (p.id, p.title))
      .toMap

    val updates = potentialUpdates.filter { id =>
      val remotePathToContentMap = remoteSolutionMap(id).map(s => (s.path, s.content)).toMap
      val localPathToContentMap = localSolutionMap(id).map(s => (s.path, s.content)).toMap

      remotePathToContentMap != localPathToContentMap
    }

    ZIO
      .logInfo(
        s"External solutions sync summary: ${insertions.size} insertions, ${updates.size} updates, ${deletions.size} deletions"
      )
      .run

    if (insertions.nonEmpty) {
      ZIO.logInfo(s"Inserting ${insertions.size} new external solutions:").run

      for (id <- insertions) {
        val problemName = problemIdToNameMap.getOrElse(id, "")

        for (remote <- remoteSolutionMap(id)) {
          val local = toDatabaseEntity(remote)
          ZIO.logInfo(s"  + [$id] $problemName").run
          solutionRepository.add(local).run
        }
      }
    }

    if (updates.nonEmpty) {
      ZIO.logInfo(s"Refreshing ${updates.size} external solutions:").run

      for (id <- updates) {
        val problemName = problemIdToNameMap.getOrElse(id, "")

        for (local <- localSolutionMap(id)) {
          solutionRepository.delete(local.uid).run
        }

        for (remote <- remoteSolutionMap(id)) {
          val local = toDatabaseEntity(remote)
          ZIO.logInfo(s"  ~ [$id] $problemName").run
          solutionRepository.add(local).run
        }
      }
    }

    if (deletions.nonEmpty) {
      ZIO.logInfo(s"Deleting ${deletions.size} external solutions:").run

      for (id <- deletions) {
        val problemName = problemIdToNameMap.getOrElse(id, "")
        for (local <- localSolutionMap(id)) {
          ZIO.logInfo(s"  - [$id] $problemName").run
          solutionRepository.delete(local.uid).run
        }
      }
    }

    if (insertions.isEmpty && updates.isEmpty && deletions.isEmpty) {
      ZIO.logInfo("No external solutions changes detected. Database is up to date.").run
    }

    ()
  }

  private def toDatabaseEntity(solution: SolutionItem) =
    SolutionEntity(
      uid = SolutionUid(UUID.randomUUID()),
      problemId = solution.problemId,
      path = solution.path,
      content = solution.content,
      sourceType = SourceType.EXTERNAL
    )

  private case class SolutionItem(
    problemId: ProblemId,
    path: String,
    content: String
  )
}
