package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.db.model.QuestionSyncEntity
import com.github.ai.leetcodequiz.data.db.repository.QuestionSyncRepository
import com.github.ai.leetcodequiz.data.file.FileSystemProvider
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID

class SyncQuestionsUseCase(
  private val fileSystemProvider: FileSystemProvider,
  private val questionSyncRepository: QuestionSyncRepository,
  private val cloneRepositoryUseCase: CloneGithubRepositoryUseCase
) {

  def syncQuestions(): IO[DomainError, Unit] = defer {
    val lastSync = questionSyncRepository.getLastSync().run

    val timeThreshold = LocalDateTime.now(ZoneOffset.UTC).minusHours(2)
    val shouldSync = lastSync.isEmpty || lastSync.get.timestamp.isBefore(timeThreshold)

    ZIO.logInfo(s"lastSyncTime: ${lastSync.map(_.timestamp)}, shouldSync: $shouldSync").run

    if (shouldSync) {
      val repoDir =
        cloneRepositoryUseCase
          .cloneRepository("https://github.com/noworneverev/leetcode-api.git")
          .run

        questionSyncRepository
          .add(
            QuestionSyncEntity(
              uid = UUID.randomUUID(),
              timestamp = LocalDateTime.now(ZoneOffset.UTC)
            )
          )
          .run
    }

    ()
  }
}
