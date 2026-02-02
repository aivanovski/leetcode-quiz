package com.github.ai.leetcodequiz.domain.jobs

import com.github.ai.leetcodequiz.data.db.model.{
  ProblemId,
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

class SyncSolutionsJob(
  private val cloneRepositoryUseCase: CloneGithubRepositoryUseCase,
  private val fileSystemProvider: FileSystemProvider,
  private val syncRepository: DataSyncRepository,
  private val solutionRepository: SolutionRepository,
  private val problemRepository: ProblemRepository
) extends ScheduledJob(
      syncRepository = syncRepository,
      interval = 4.hours,
      syncType = SyncType.SOLUTIONS,
      dependsOn = List(SyncType.PROBLEMS)
    ) {

  override def run(): IO[DomainError, Unit] = defer {
    val syncUid = onSyncStart().run

    val destinationDirPath = RelativePath(s"github/${syncUid.toString}")

    val repoDir = cloneRepositoryUseCase
      .cloneRepository(
        repositoryUrl = "https://github.com/aivanovski/leetcode-notes.git",
        destinationDirPath = destinationDirPath
      )
      .run

    val files = fileSystemProvider
      .listFileTree(repoDir, maxDepth = 8)
      .run
      .flatten

    val filesAndProblemIds = files
      .flatMap { file =>
        parseProblemId(file.getName())
          .map(id => (file, ProblemId(id)))
      }

    val localSolutions = solutionRepository.getAll().run
    val remoteSolutions = ZIO.collectAll {
      filesAndProblemIds.map { (file, problemId) =>
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

  private def parseProblemId(fileName: String): Option[Int] = {
    if (!fileName.endsWith(".kt")) return None

    val idEndIndex = fileName.indexOf("-")
    if (idEndIndex == -1) return None

    val idStr = fileName.substring(0, idEndIndex).trim
    idStr.toIntOption
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
      val remoteNameToSolutionMap = remoteSolutionMap(id).groupBy(s => s.path)
      val localNameToSolutionMap = localSolutionMap(id).groupBy(s => s.path)

      // TODO: implement solutions update
      false
    }

    ZIO
      .logInfo(
        s"Solutions sync summary: ${insertions.size} insertions, ${updates.size} updates, ${deletions.size} deletions"
      )
      .run

    if (insertions.nonEmpty) {
      ZIO.logInfo(s"Inserting ${insertions.size} new solutions:").run

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
      ZIO.logInfo(s"Updating ${updates.size} solutions:").run

      for (id <- updates) {
        val name = problemIdToNameMap.getOrElse(id, "")
//        val remote = remoteSolutionMap(id)
//        val local = localSolutionMap(id)
//        val updated = local.copy(
//          content = remote.content
//        )
//        ZIO.logInfo(s"  ~ [$id] $name").run
//        solutionRepository.update(updated).run
      }
    }

    if (deletions.nonEmpty) {
      ZIO.logInfo(s"Deleting ${deletions.size} solutions:").run

      for (id <- deletions) {
        val problemName = problemIdToNameMap.getOrElse(id, "")
        for (local <- localSolutionMap(id)) {
          ZIO.logInfo(s"  - [$id] $problemName").run
          solutionRepository.delete(local.uid).run
        }
      }
    }

    if (insertions.isEmpty && updates.isEmpty && deletions.isEmpty) {
      ZIO.logInfo("No solutions changes detected. Database is up to date.").run
    }

    ()
  }

  private def toDatabaseEntity(solution: SolutionItem) =
    SolutionEntity(
      uid = SolutionUid(UUID.randomUUID()),
      problemId = solution.problemId,
      path = solution.path,
      content = solution.content
    )

  private case class SolutionItem(
    problemId: ProblemId,
    path: String,
    content: String
  )
}
