package com.github.ai.leetcodequiz

import com.github.ai.leetcodequiz.domain.{CliArgumentParser, ScheduledJobService, StartupService}
import com.github.ai.leetcodequiz.entity.{CliArguments, JwtData}
import com.github.ai.leetcodequiz.entity.HttpProtocol.{HTTP, HTTPS}
import com.github.ai.leetcodequiz.presentation.routes.{
  ProblemRoutes,
  QuestionRoutes,
  QuestionnaireRoutes,
  UnansweredQuestionnaireRoutes,
  AuthRoutes
}
import com.github.ai.leetcodequiz.data.db.DoobieTransactor
import com.github.ai.leetcodequiz.utils.RequestLogger
import zio.*
import zio.http.*
import zio.logging.{LogColor, LogFormat, LoggerNameExtractor}
import zio.logging.backend.SLF4J
import zio.direct.*

import java.time.format.DateTimeFormatter

object Main extends ZIOAppDefault {

  private val routes =
    (ProblemRoutes.routes()
      ++ QuestionRoutes.routes()
      ++ QuestionnaireRoutes.routes()
      ++ UnansweredQuestionnaireRoutes.routes()
      ++ AuthRoutes.routes())
      @@ RequestLogger.requestLogger

  override val bootstrap: ZLayer[Any, Nothing, Unit] = {
    val logFormat: LogFormat =
      LogFormat
        .timestamp(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssAZ"))
        .highlight(_ => LogColor.BLUE)
        |-| LogFormat.bracketStart + LogFormat.loggerName(
          LoggerNameExtractor.trace
        ) + LogFormat.bracketEnd |-|
        LogFormat.fiberId |-| LogFormat.level.highlight |-| LogFormat.line.highlight

    Runtime.removeDefaultLoggers >>> SLF4J.slf4j(logFormat)
  }

  private def application() = defer {
    val startupService = ZIO.service[StartupService].run

    startupService.startupServer().run

    Server.serve(routes).run

    ()
  }

  private def createServerConfig(
    arguments: CliArguments
  ) = defer {
    arguments.protocol match {
      case HTTP =>
        Server.Config.default
          .port(arguments.getPort())

      case HTTPS =>
        Server.Config.default
          .port(arguments.getPort())
          .ssl(SSLConfig.fromFile("dev-data/server.crt", "dev-data/server.key"))
    }
  }

  override def run: ZIO[ZIOAppArgs, Throwable, Unit] = defer {
    val arguments = CliArgumentParser().parse().run

    ZIO.logInfo(s"Starting server on port ${arguments.getPort()}").run
    ZIO.logInfo(s"   environment=${arguments.environment}").run
    ZIO.logInfo(s"   protocol=${arguments.protocol}").run

    val serverConfig = createServerConfig(arguments).run

    application()
      .provide(
        // Application arguments
        ZLayer.succeed(arguments),
        ZLayer.succeed(JwtData.DEFAULT),

        // Use-Cases
        Layers.cloneGithubRepositoryUseCase,
        Layers.createNewQuestionnaireUseCase,
        Layers.submitQuestionAnswerUseCase,
        Layers.setupTestDataUseCase,
        Layers.selectNextQuestionsUseCase,
        Layers.getRemainedQuestionsUseCase,

        // Controllers
        Layers.problemController,
        Layers.questionController,
        Layers.questionnaireController,
        Layers.unansweredQuestionnaireController,
        Layers.userController,

        // Scheduled jobs
        Layers.syncProblemsJob,
        Layers.syncQuestionsJob,

        // Services
        Layers.startupService,
        Layers.scheduledJobService,
        Layers.passwordService,
        Layers.jwtTokeService,
        Layers.accessResolver,

        // Repositories
        Layers.dataSyncRepository,
        Layers.problemRepository,
        Layers.questionRepository,
        Layers.questionnaireRepository,
        Layers.submissionRepository,
        Layers.userRepository,

        // Dao
        Layers.dataSyncDao,
        Layers.problemDao,
        Layers.problemHintDao,
        Layers.questionDao,
        Layers.questionnaireDao,
        Layers.submissionDao,
        Layers.userDao,
        Layers.nextQuestionDao,

        // Others
        Layers.jsonSerializer,
        Layers.fileSystemProvider,
        Layers.problemParser,
        Server.live,
        ZLayer.succeed(serverConfig),
        DoobieTransactor.layer("db")
      )
      .run
    ()
  }
}
