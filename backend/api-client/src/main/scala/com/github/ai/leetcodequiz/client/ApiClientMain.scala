package com.github.ai.leetcodequiz.client

import com.github.ai.leetcodequiz.client.ApiClientMain.getArgs
import com.github.ai.leetcodequiz.client.utils.Printer
import zio.*
import zio.direct.*
import zio.http.*

object ApiClientMain extends ZIOAppDefault {

  private val HelpText =
    """
      |Commands:
      |
      |questions                                             Get list of questions
      |help                                                  Print help
      |""".stripMargin

  class InvalidCliArgumentException(message: String) extends Exception(message)
  class EmptyCliArgumentException extends InvalidCliArgumentException("Empty arguments")

  override def run: ZIO[ZIOAppArgs, Any, ExitCode] = {
    val application = for {
      arguments <- getArgs.map(_.toList.mkString(" "))
      result <- processArguments(arguments)
        .provide(
          Client.default,
          Scope.default,
          ZLayer.succeed(Printer()),
          ZLayer.fromFunction(ApiClient(_))
        )
    } yield result

    application
      .catchAll { error =>
        defer {
          if (!error.isInstanceOf[EmptyCliArgumentException]) {
            Console.printLine(s"Error: $error").run
          }

          if (error.isInstanceOf[InvalidCliArgumentException]) {
            Console.printLine(HelpText).run
          }

          ExitCode.failure
        }
      }
  }

  private def processArguments(arguments: String) = defer {
    val api = ZIO.service[ApiClient].run
    val printer = ZIO.service[Printer].run

    if (arguments.isBlank) {
      ZIO.fail(EmptyCliArgumentException()).run
    }

    val response = arguments match {
      case s"questions" => api.getQuestions().run
      case _ => ZIO.fail(InvalidCliArgumentException(s"Illegal arguments: $arguments")).run
    }

    printer.print(response).run

    ExitCode.success
  }
}
