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
      |signup $NAME $EMAIL $PASSWORD             Create new user
      |login                                     Login with default credentials
      |login $EMAIL $PASSWORD                    Login with specified credentials
      |problems                                  Get list of problems
      |problem ID                                Get problem by ID
      |questions                                 Get list of questions
      |questionnaires                            Get list of questionnaires
      |unanswered QUESTIONNAIRE_ID               Get unanswered questions for questionnaire
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
      case s"signup $name $email $password" => api.signup(name, email, password).run
      case "login" => api.login().run
      case s"login $email $password" => api.login(email, password).run
      case s"problems" => api.getAuthToken().flatMap(token => api.getProblems(token)).run
      case s"problem $id" => api.getAuthToken().flatMap(token => api.getProblem(id, token)).run
      case s"questions" => api.getAuthToken().flatMap(token => api.getQuestions(token)).run
      case s"questionnaire $id" => api.getAuthToken().flatMap(t => api.getQuestionnaire(id, t)).run
      case s"questionnaires" => api.getAuthToken().flatMap(t => api.getQuestionnaires(t)).run
      case s"unanswered $questionnaireId" =>
        api.getAuthToken().flatMap(t => api.getUnanswered(questionnaireId, t)).run
      case s"answer $questionnaireId" =>
        api.getAuthToken().flatMap(t => answer(api, questionnaireId, t)).run
      case _ => ZIO.fail(InvalidCliArgumentException(s"Illegal arguments: $arguments")).run
    }

    printer.print(response).run

    ExitCode.success
  }

  private def answer(
    api: ApiClient,
    questionnareId: String,
    authToken: String
  ): ApiResponse = defer {
    val questionId = api.getFirstQuestionIdFromQuestionnaire(questionnareId, authToken).run

    api
      .postAnswer(
        questionnaireId = questionnareId,
        questionId = questionId,
        answer = 1,
        authToken = authToken
      )
      .run
  }
}
