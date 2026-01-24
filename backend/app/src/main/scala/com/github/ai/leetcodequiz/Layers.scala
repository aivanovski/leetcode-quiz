package com.github.ai.leetcodequiz

import com.github.ai.leetcodequiz.data.db.dao.{
  DataSyncEntityDao,
  NextQuestionEntityDao,
  ProblemEntityDao,
  ProblemHintEntityDao,
  QuestionEntityDao,
  QuestionnaireEntityDao,
  SubmissionEntityDao,
  UserEntityDao
}
import com.github.ai.leetcodequiz.data.db.repository.{
  DataSyncRepository,
  ProblemRepository,
  QuestionRepository,
  QuestionnaireRepository,
  SubmissionRepository,
  UserRepository
}
import com.github.ai.leetcodequiz.data.file.{FileSystemProvider, FileSystemProviderImpl}
import com.github.ai.leetcodequiz.data.json.{JsonSerializer, ProblemParser}
import com.github.ai.leetcodequiz.domain.authentication.{AccessResolver, JwtTokeService}
import com.github.ai.leetcodequiz.domain.jobs.{SyncProblemsJob, SyncQuestionsJob}
import com.github.ai.leetcodequiz.domain.usecases.{
  CloneGithubRepositoryUseCase,
  CreateNewQuestionnaireUseCase,
  GetRemainedQuestionsUseCase,
  SelectNextQuestionsUseCase,
  SetupTestDataUseCase,
  SubmitQuestionAnswerUseCase,
  ValidateEmailUseCase
}
import com.github.ai.leetcodequiz.domain.{PasswordService, ScheduledJobService, StartupService}
import com.github.ai.leetcodequiz.entity.JwtData
import com.github.ai.leetcodequiz.presentation.controllers.{
  AuthController,
  ProblemController,
  QuestionController,
  QuestionnaireController,
  UnasweredQuestionnareController
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
  val userDao = ZLayer.fromFunction(UserEntityDao(_))
  val nextQuestionDao = ZLayer.fromFunction(NextQuestionEntityDao(_))

  // Repositories
  val dataSyncRepository = ZLayer.fromFunction(DataSyncRepository(_))
  val problemRepository = ZLayer.fromFunction(ProblemRepository(_, _))
  val questionRepository = ZLayer.fromFunction(QuestionRepository(_))
  val questionnaireRepository = ZLayer.fromFunction(QuestionnaireRepository(_, _))
  val submissionRepository = ZLayer.fromFunction(SubmissionRepository(_))
  val userRepository = ZLayer.fromFunction(UserRepository(_))

  // Services
  val passwordService = ZLayer.succeed(PasswordService())
  val jwtTokeService = ZLayer.fromFunction(JwtTokeService(_, _))
  val startupService = ZLayer.succeed(StartupService())
  val scheduledJobService = ZLayer.succeed(ScheduledJobService())
  val accessResolver = ZLayer.fromFunction(AccessResolver(_, _))

  // Scheduled jobs
  val syncProblemsJob = ZLayer.fromFunction(SyncProblemsJob(_, _, _, _, _))
  val syncQuestionsJob = ZLayer.fromFunction(SyncQuestionsJob(_, _, _, _, _))

  // Use cases
  val cloneGithubRepositoryUseCase = ZLayer.fromFunction(CloneGithubRepositoryUseCase(_))
  val createNewQuestionnaireUseCase = ZLayer.fromFunction(CreateNewQuestionnaireUseCase(_, _, _))
  val submitQuestionAnswerUseCase = ZLayer.fromFunction(SubmitQuestionAnswerUseCase(_, _, _, _, _))
  val setupTestDataUseCase = ZLayer.fromFunction(SetupTestDataUseCase(_, _))
  val validateEmailUseCase = ZLayer.succeed(ValidateEmailUseCase())
  val getRemainedQuestionsUseCase = ZLayer.fromFunction(GetRemainedQuestionsUseCase(_, _, _))
  val selectNextQuestionsUseCase = ZLayer.fromFunction(SelectNextQuestionsUseCase(_))

  // Response use cases

  // Controllers
  val problemController = ZLayer.fromFunction(ProblemController(_, _))
  val questionController = ZLayer.fromFunction(QuestionController(_, _, _))
  val questionnaireController = ZLayer.fromFunction(QuestionnaireController(_, _, _, _, _, _, _))
  val unansweredQuestionnaireController =
    ZLayer.fromFunction(UnasweredQuestionnareController(_, _, _, _, _))
  val userController = ZLayer.fromFunction(AuthController(_, _, _, _))

  // Other
  val jsonSerializer = ZLayer.succeed(JsonSerializer())
  val problemParser = ZLayer.fromFunction(ProblemParser(_))
  val fileSystemProvider = ZLayer.succeed[FileSystemProvider](FileSystemProviderImpl())
}
