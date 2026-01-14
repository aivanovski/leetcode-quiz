package com.github.ai.leetcodequiz

import com.github.ai.leetcodequiz.domain.{CliArgumentParser, ScheduledJobService, StartupService}
import com.github.ai.leetcodequiz.entity.CliArguments
import com.github.ai.leetcodequiz.entity.HttpProtocol.{HTTP, HTTPS}
import com.github.ai.leetcodequiz.presentation.routes.QuestionRoutes
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.*
import zio.http.*
import zio.logging.LogFormat
import zio.logging.backend.SLF4J
import zio.direct.*

object Main extends ZIOAppDefault {

  private val routes = QuestionRoutes.routes()

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
        Layers.syncQuestionsUseCase,

        // Response assemblers use cases
//        Layers.assembleGroupResponseUseCase,
//        Layers.assembleGroupsResponseUseCase,
//        Layers.assembleExpenseUseCase,

        // Controllers
//        Layers.memberController,
//        Layers.groupController,
//        Layers.expenseController,
        Layers.currencyController,

        // Services
        Layers.passwordService,
        Layers.accessResolverService,
        Layers.startupService,
        Layers.scheduledJobService,

        // Repositories
        Layers.questionSyncRepository,
//        Layers.groupRepository,
//        Layers.currencyRepository,

        // Dao
        Layers.userDao,
        Layers.questionSyncDao,

        // Others
        Layers.fileSystemProvider,
        Layers.jsonSerialized,
        Server.live,
        ZLayer.succeed(serverConfig),
        Quill.H2.fromNamingStrategy(SnakeCase),
        if (arguments.isUseInMemoryDatabase) {
          Quill.DataSource.fromPrefix("test-h2db")
        } else {
          Quill.DataSource.fromPrefix("h2db")
        }
      )
    } yield ()
  }
}
