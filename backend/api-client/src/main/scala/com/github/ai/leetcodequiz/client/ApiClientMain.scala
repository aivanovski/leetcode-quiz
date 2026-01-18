package com.github.ai.leetcodequiz.client

import com.github.ai.leetcodequiz.api.response.PostSubmissionResponse
import com.github.ai.leetcodequiz.client.ApiClientMain.getArgs
import com.github.ai.leetcodequiz.client.utils.Printer
import com.google.gson.reflect.TypeToken
import zio.*
import zio.direct.*
import zio.http.*

object ApiClientMain extends ZIOAppDefault {

  private val HelpText =
    """
      |Commands:
      |
      |problems                                  Get list of problems
      |problem ID                                Get problem by ID
      |questionnaires                            Get list of questionnaires
      |answer QUESTIONNAIRE_ID                   Send answer to questionnaire with QUESTIONNAIRE_ID
      |help                                      Print help
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
      case s"problems" => api.getProblems().run
      case s"problem $id" => api.getProblem(id).run
      case s"questionnaire $id" => api.getQuestionnaire(id).run
      case s"questionnaires" => api.getQuestionnaires().run
      case s"answer $questionnaireId" => answer(api, questionnaireId).run
      case _ => ZIO.fail(InvalidCliArgumentException(s"Illegal arguments: $arguments")).run
    }

    printer.print(response).run

    ExitCode.success
  }

  private def answer(
    api: ApiClient,
    questionnareId: String
  ): ApiResponse = defer {
    val questionId = api
      .getQuestionnaire(questionnareId)
      .flatMap(response => response.body.asString)
      .flatMap { content =>
        ZIO.attempt(
          api.gson.fromJson(content, TypeToken.get(classOf[PostSubmissionResponse]))
        )
      }
      .map(response => response.questionnaire().nextQuestions().getFirst().id())
      .run

    ZIO.logInfo(s"id=$questionnareId, questionId=$questionId").run

    api
      .postAnswer(
        questionnaireId = questionnareId,
        questionId = questionId,
        answer = 1
      )
      .run
  }
}
