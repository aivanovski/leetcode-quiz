package com.github.ai.leetcodequiz

import com.github.ai.leetcodequiz.data.JsonSerializer
import com.github.ai.leetcodequiz.data.db.dao.{QuestionSyncEntityDao, UserEntityDao}
import com.github.ai.leetcodequiz.data.db.repository.QuestionSyncRepository
import com.github.ai.leetcodequiz.data.file.{FileSystemProvider, FileSystemProviderImpl}
import com.github.ai.leetcodequiz.domain.usecases.{
  CloneGithubRepositoryUseCase,
  SyncQuestionsUseCase
}
import com.github.ai.leetcodequiz.domain.{
  AccessResolverService,
  PasswordService,
  ScheduledJobService,
  ScheduledJobServiceImpl,
  StartupService
}
import com.github.ai.leetcodequiz.presentation.controllers.QuestionController
import zio.{ZIO, ZLayer}

object Layers {

  // Dao's
  val userDao = ZLayer.fromFunction(UserEntityDao(_))
  val questionSyncDao = ZLayer.fromFunction(QuestionSyncEntityDao(_))

  // Repositories
  val questionSyncRepository = ZLayer.fromFunction(QuestionSyncRepository(_))

  // Services
  val passwordService = ZLayer.succeed(PasswordService())
  val accessResolverService = ZLayer.fromFunction(AccessResolverService(_))
  val startupService = ZLayer.succeed(StartupService())
  val scheduledJobService: ZLayer[SyncQuestionsUseCase, Nothing, ScheduledJobService] =
    ZLayer.fromFunction(ScheduledJobServiceImpl(_))

  // Use cases
  val cloneGithubRepositoryUseCase = ZLayer.fromFunction(CloneGithubRepositoryUseCase(_))
  val syncQuestionsUseCase = ZLayer.fromFunction(SyncQuestionsUseCase(_, _, _))

  // Response use cases

  // Controllers
  val currencyController = ZLayer.fromFunction(QuestionController(_))

  // Other
  val jsonSerialized = ZLayer.succeed(JsonSerializer())
  val fileSystemProvider = ZLayer.succeed[FileSystemProvider](FileSystemProviderImpl())
}
