package com.github.ai.leetcodequiz

import com.github.ai.leetcodequiz.domain.{ApplicationConfigLoader, StartupService}
import com.github.ai.leetcodequiz.entity.ApplicationConfig
import com.github.ai.leetcodequiz.entity.HttpProtocol.{HTTP, HTTPS}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.presentation.routes.{
  AuthRoutes,
  ProblemRoutes,
  QuestionRoutes,
  QuestionnaireRoutes
}
import com.github.ai.leetcodequiz.utils.RequestLogger
import zio.{Console, ExitCode, Runtime, UIO, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}
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

  private def createZIOServerConfig(config: ApplicationConfig) = defer {
    config.server.protocol match {
      case HTTP =>
        Server.Config.default
          .port(8080)

      case HTTPS =>
        Server.Config.default
          .port(8443)
          .ssl(SSLConfig.fromFile("dev-data/server.crt", "dev-data/server.key"))
    }
  }

  private def printStartupOptions(
    httpPort: Int,
    config: ApplicationConfig
  ): UIO[Unit] = defer {
    val text = s"""
      |Starting server on port $httpPort
      |    environment=${config.environment}
      |    protocol=${config.server.protocol}
      |    db.url=${config.database.url}
      |    jwt.issuer=${config.jwt.issuer}
      |    users=${config.debugUsers.map(_.email)}
      |""".stripMargin

    val lines = ZIO.succeed(text.split("\n")).run
    ZIO.foreach(lines) { line => ZIO.logInfo(line) }.run

    ()
  }

  override def run: ZIO[ZIOAppArgs, Throwable, Unit] = defer {
    val appConfig = ApplicationConfigLoader().loadConfig().run

    val protocol = appConfig.server.protocol
    val port = protocol match {
      case HTTP => 8080
      case HTTPS => 8443
    }

    printStartupOptions(port, appConfig).run

    val serverConfig = createZIOServerConfig(appConfig).run

    application()
      .provide(
        // Application config
        ZLayer.succeed(appConfig),

        // Use-Cases
        Layers.cloneGithubRepositoryUseCase,
        Layers.downloadFileUseCase,
        Layers.createNewQuestionnaireUseCase,
        Layers.submitQuestionAnswerUseCase,
        Layers.setupTestDataUseCase,
        Layers.selectNextQuestionsUseCase,
        Layers.getRemainedQuestionsUseCase,
        Layers.getQuestionnaireStatsUseCase,

        // Controllers
        Layers.problemController,
        Layers.questionController,
        Layers.questionnaireController,
        Layers.answerController,
        Layers.userController,

        // Scheduled jobs
        Layers.syncProblemsJob,
        Layers.syncQuestionsJob,
        Layers.syncPersonalSolutionsJob,
        Layers.syncExternalSolutionsJob,

        // Services
        Layers.startupService,
        Layers.scheduledJobService,
        Layers.passwordService,
        Layers.authService,

        // Repositories
        Layers.dataSyncRepository,
        Layers.problemRepository,
        Layers.questionRepository,
        Layers.questionnaireRepository,
        Layers.userRepository,
        Layers.solutionRepository,

        // Dao
        Layers.dataSyncDao,
        Layers.problemDao,
        Layers.problemHintDao,
        Layers.questionDao,
        Layers.questionnaireDao,
        Layers.answerDao,
        Layers.userDao,
        Layers.nextQuestionDao,
        Layers.solutionDao,

        // Others
        Layers.database,
        Layers.jsonSerializer,
        Layers.fileSystemProvider,
        Client.default,
        Layers.streamingProblemParser,
        Server.live,
        ZLayer.succeed(serverConfig)
      )
      .run
    ()
  }.catchAll { error =>
    Console.printLineError(s"Application failure: $error") *>
      ZIO.fail(Throwable(error))
  }
}
