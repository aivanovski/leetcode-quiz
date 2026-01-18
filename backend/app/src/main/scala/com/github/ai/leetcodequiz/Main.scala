package com.github.ai.leetcodequiz

import com.github.ai.leetcodequiz.domain.{CliArgumentParser, ScheduledJobService, StartupService}
import com.github.ai.leetcodequiz.entity.CliArguments
import com.github.ai.leetcodequiz.entity.HttpProtocol.{HTTP, HTTPS}
import com.github.ai.leetcodequiz.presentation.routes.{
  ProblemRoutes,
  QuestionRoutes,
  QuestionnaireRoutes
}
import com.github.ai.leetcodequiz.data.doobie.DoobieTransactor
import zio.*
import zio.http.*
import zio.logging.LogFormat
import zio.logging.backend.SLF4J
import zio.direct.*

object Main extends ZIOAppDefault {

  private val routes = ProblemRoutes.routes()
    ++ QuestionRoutes.routes()
    ++ QuestionnaireRoutes.routes()

  override val bootstrap: ZLayer[Any, Nothing, Unit] = {
    Runtime.removeDefaultLoggers >>> SLF4J.slf4j(LogFormat.colored)
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

  override def run: ZIO[ZIOAppArgs, Throwable, Unit] = {
    for {
      arguments <- CliArgumentParser().parse()
      _ <- ZIO.logInfo(s"Starting server on port ${arguments.getPort()}")
      _ <- ZIO.logInfo(s"   isUseInMemoryDatabase=${arguments.isUseInMemoryDatabase}")
      _ <- ZIO.logInfo(s"   isPopulateTestData=${arguments.isPopulateTestData}")
      _ <- ZIO.logInfo(s"   protocol=${arguments.protocol}")

      serverConfig <- createServerConfig(arguments)

      _ <- application().provide(
        // Application arguments
        ZLayer.succeed(arguments),

        // Use-Cases
        Layers.cloneGithubRepositoryUseCase,
        Layers.createNewQuestionnaireUseCase,
        Layers.submitQuestionAnswerUseCase,

        // Controllers
        Layers.problemController,
        Layers.questionController,
        Layers.questionnaireController,

        // Scheduled jobs
        Layers.syncProblemsJob,
        Layers.syncQuestionsJob,

        // Services
        Layers.passwordService,
        Layers.startupService,
        Layers.scheduledJobService,

        // Repositories
        Layers.dataSyncRepository,
        Layers.problemRepository,
        Layers.questionRepository,
        Layers.questionnaireRepository,
        Layers.submissionRepository,

        // Dao
        Layers.dataSyncDao,
        Layers.problemDao,
        Layers.problemHintDao,
        Layers.questionDao,
        Layers.questionnaireDao,
        Layers.submissionDao,

        // Others
        Layers.fileSystemProvider,
        Layers.jsonSerialized,
        Layers.problemParser,
        Server.live,
        ZLayer.succeed(serverConfig),
        DoobieTransactor.layer("db")
      )
    } yield ()
  }
}
