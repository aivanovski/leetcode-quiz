package com.github.ai.leetcodequiz

import com.github.ai.leetcodequiz.data.db.dao.{
  DataSyncEntityDao,
  ProblemEntityDao,
  ProblemHintEntityDao,
  QuestionEntityDao,
  QuestionnaireEntityDao,
  SubmissionEntityDao
}
import com.github.ai.leetcodequiz.data.db.repository.{
  DataSyncRepository,
  ProblemRepository,
  QuestionRepository,
  QuestionnaireRepository,
  SubmissionRepository
}
import com.github.ai.leetcodequiz.data.file.{FileSystemProvider, FileSystemProviderImpl}
import com.github.ai.leetcodequiz.data.json.{JsonSerializer, ProblemParser}
import com.github.ai.leetcodequiz.domain.jobs.{SyncProblemsJob, SyncQuestionsJob}
import com.github.ai.leetcodequiz.domain.usecases.{
  CloneGithubRepositoryUseCase,
  CreateNewQuestionnaireUseCase,
  SubmitQuestionAnswerUseCase
}
import com.github.ai.leetcodequiz.domain.{PasswordService, ScheduledJobService, StartupService}
import com.github.ai.leetcodequiz.presentation.controllers.{
  ProblemController,
  QuestionController,
  QuestionnaireController
}
import zio.{ZIO, ZLayer}

object Layers {

  // Dao's
  val dataSyncDao = ZLayer.fromFunction(DataSyncEntityDao(_))
  val problemDao = ZLayer.fromFunction(ProblemEntityDao(_))
  val problemHintDao = ZLayer.fromFunction(ProblemHintEntityDao(_))
  val questionDao = ZLayer.fromFunction(QuestionEntityDao(_))
  val questionnaireDao = ZLayer.fromFunction(QuestionnaireEntityDao(_))
  val submissionDao = ZLayer.fromFunction(SubmissionEntityDao(_))

  // Repositories
  val dataSyncRepository = ZLayer.fromFunction(DataSyncRepository(_))
  val problemRepository = ZLayer.fromFunction(ProblemRepository(_, _))
  val questionRepository = ZLayer.fromFunction(QuestionRepository(_))
  val questionnaireRepository = ZLayer.fromFunction(QuestionnaireRepository(_))
  val submissionRepository = ZLayer.fromFunction(SubmissionRepository(_))

  // Services
  val passwordService = ZLayer.succeed(PasswordService())
  val startupService = ZLayer.succeed(StartupService())
  val scheduledJobService = ZLayer.succeed(ScheduledJobService())

  // Scheduled jobs
  val syncProblemsJob = ZLayer.fromFunction(SyncProblemsJob(_, _, _, _, _))
  val syncQuestionsJob = ZLayer.fromFunction(SyncQuestionsJob(_, _, _, _, _))

  // Use cases
  val cloneGithubRepositoryUseCase = ZLayer.fromFunction(CloneGithubRepositoryUseCase(_))
  val createNewQuestionnaireUseCase = ZLayer.fromFunction(CreateNewQuestionnaireUseCase(_, _))
  val submitQuestionAnswerUseCase = ZLayer.fromFunction(SubmitQuestionAnswerUseCase(_, _, _))

  // Response use cases

  // Controllers
  val problemController = ZLayer.fromFunction(ProblemController(_, _))
  val questionController = ZLayer.fromFunction(QuestionController(_, _, _))
  val questionnaireController = ZLayer.fromFunction(QuestionnaireController(_, _, _, _, _, _))

  // Other
  val jsonSerialized = ZLayer.succeed(JsonSerializer())
  val problemParser = ZLayer.succeed(ProblemParser())
  val fileSystemProvider = ZLayer.succeed[FileSystemProvider](FileSystemProviderImpl())
}
