package com.github.ai.leetcodequiz

import com.github.ai.leetcodequiz.data.db.{AppDatabase, DatabaseConnectionFactory}
import com.github.ai.leetcodequiz.data.db.dao.{
  AnswerEntityDao,
  DataSyncEntityDao,
  NextQuestionEntityDao,
  ProblemEntityDao,
  ProblemHintEntityDao,
  QuestionEntityDao,
  QuestionnaireEntityDao,
  SolutionEntityDao,
  UserEntityDao
}
import com.github.ai.leetcodequiz.data.db.repository.{
  DataSyncRepository,
  ProblemRepository,
  QuestionRepository,
  QuestionnaireRepository,
  SolutionRepository,
  UserRepository
}
import com.github.ai.leetcodequiz.data.file.{FileSystemProvider, FileSystemProviderImpl}
import com.github.ai.leetcodequiz.data.json.{JsonSerializer, ProblemParser, StreamingProblemParser}
import com.github.ai.leetcodequiz.domain.authentication.AuthService
import com.github.ai.leetcodequiz.domain.jobs.{SyncProblemsJob, SyncQuestionsJob, SyncSolutionsJob}
import com.github.ai.leetcodequiz.domain.usecases.{
  CloneGithubRepositoryUseCase,
  CreateNewQuestionnaireUseCase,
  DownloadFileUseCase,
  GetQuestionnaireStatsUseCase,
  GetRemainedQuestionsUseCase,
  SelectNextQuestionsUseCase,
  SetupTestDataUseCase,
  SubmitQuestionAnswerUseCase,
  ValidateEmailUseCase
}
import com.github.ai.leetcodequiz.domain.{PasswordService, ScheduledJobService, StartupService}
import com.github.ai.leetcodequiz.entity.ApplicationConfig
import com.github.ai.leetcodequiz.presentation.controllers.{
  AnswerController,
  AuthController,
  ProblemController,
  QuestionController,
  QuestionnaireController
}
import zio.{ZIO, ZLayer}
import zio.http.Client
import zio.direct.*

object Layers {

  // Database
  val database = ZLayer.scoped {
    defer {
      val appConfig = ZIO.service[ApplicationConfig].run

      DatabaseConnectionFactory(appConfig.database)
        .create()
        .map(db => AppDatabase(db))
        .run
    }
  }

  // Dao's
  val dataSyncDao = ZLayer.fromFunction(DataSyncEntityDao(_))
  val problemDao = ZLayer.fromFunction(ProblemEntityDao(_))
  val problemHintDao = ZLayer.fromFunction(ProblemHintEntityDao(_))
  val questionDao = ZLayer.fromFunction(QuestionEntityDao(_))
  val questionnaireDao = ZLayer.fromFunction(QuestionnaireEntityDao(_))
  val answerDao = ZLayer.fromFunction(AnswerEntityDao(_))
  val userDao = ZLayer.fromFunction(UserEntityDao(_))
  val nextQuestionDao = ZLayer.fromFunction(NextQuestionEntityDao(_))
  val solutionDao = ZLayer.fromFunction(SolutionEntityDao(_))

  // Repositories
  val dataSyncRepository = ZLayer.fromFunction(DataSyncRepository(_))
  val problemRepository = ZLayer.fromFunction(ProblemRepository(_, _))
  val questionRepository = ZLayer.fromFunction(QuestionRepository(_))
  val questionnaireRepository = ZLayer.fromFunction(QuestionnaireRepository(_, _, _))
  val userRepository = ZLayer.fromFunction(UserRepository(_))
  val solutionRepository = ZLayer.fromFunction(SolutionRepository(_))

  // Services
  val passwordService = ZLayer.succeed(PasswordService())
  val authService = ZLayer.fromFunction(AuthService(_, _))
  val startupService = ZLayer.succeed(StartupService())
  val scheduledJobService = ZLayer.fromFunction(ScheduledJobService(_))

  // Scheduled jobs
  val syncProblemsJob = ZLayer.fromFunction(SyncProblemsJob(_, _, _, _, _, _))
  val syncQuestionsJob = ZLayer.fromFunction(SyncQuestionsJob(_, _, _, _, _))
  val syncSolutionsJob = ZLayer.fromFunction(SyncSolutionsJob(_, _, _, _, _))

  // Use cases
  val cloneGithubRepositoryUseCase = ZLayer.fromFunction(CloneGithubRepositoryUseCase(_))
  val createNewQuestionnaireUseCase = ZLayer.fromFunction(CreateNewQuestionnaireUseCase(_, _, _))
  val submitQuestionAnswerUseCase = ZLayer.fromFunction(SubmitQuestionAnswerUseCase(_, _, _, _))
  val setupTestDataUseCase = ZLayer.fromFunction(SetupTestDataUseCase(_, _, _))
  val validateEmailUseCase = ZLayer.succeed(ValidateEmailUseCase())
  val getRemainedQuestionsUseCase = ZLayer.fromFunction(GetRemainedQuestionsUseCase(_, _))
  val selectNextQuestionsUseCase = ZLayer.fromFunction(SelectNextQuestionsUseCase(_, _, _))
  val getQuestionnaireStatsUseCase = ZLayer.fromFunction(GetQuestionnaireStatsUseCase(_, _))
  val downloadFileUseCase = ZLayer.fromFunction(DownloadFileUseCase(_, _))

  // Controllers
  val problemController = ZLayer.fromFunction(ProblemController(_, _, _, _))
  val questionController = ZLayer.fromFunction(QuestionController(_, _, _))
  val questionnaireController = ZLayer.fromFunction(QuestionnaireController(_, _, _, _, _, _, _))
  val answerController = ZLayer.fromFunction(AnswerController(_, _, _, _))
  val userController = ZLayer.fromFunction(AuthController(_, _, _, _))

  // Other
  val jsonSerializer = ZLayer.succeed(JsonSerializer())
  val problemParser = ZLayer.fromFunction(ProblemParser(_))
  val streamingProblemParser = ZLayer.succeed(StreamingProblemParser())
  val fileSystemProvider = ZLayer.succeed[FileSystemProvider](FileSystemProviderImpl())
}
