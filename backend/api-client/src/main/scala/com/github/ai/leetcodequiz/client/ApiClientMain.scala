package com.github.ai.leetcodequiz.client

import com.github.ai.leetcodequiz.client.ApiClientMain.getArgs
import com.github.ai.leetcodequiz.client.utils.Printer
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}
import scala.jdk.CollectionConverters.*
import zio.*
import zio.direct.*
import zio.http.*

object ApiClientMain extends ZIOAppDefault {

  private val HelpText =
    """
      |Commands:
      |
      |Options:
      |--debug                                  Use debug server (default): https://127.0.0.1:8443
      |--prod                                   Use production server
      |
      |signup $NAME $EMAIL $PASSWORD             Create new user
      |login                                     Login with default credentials
      |login $EMAIL $PASSWORD                    Login with specified credentials
      |problems                                  Get list of problems
      |problem ID                                Get problem by ID
      |questions                                 Get list of questions
      |questionnaires                            Get list of questionnaires
      |unanswered QUESTIONNAIRE_ID               Get unanswered questions for questionnaire
      |answer QUESTIONNAIRE_ID                   Send positive answer to first question in questionnaire
      |answer TIMES QUESTIONNAIRE_ID             Send positive answer to first question in questionnaire number of times
      |create-debug-users                        Read DEBUG_USERS/DEBUG_PASSWORDS from .env and send signup requests
      |help                                      Print help
      |""".stripMargin

  class InvalidCliArgumentException(message: String) extends Exception(message)
  class EmptyCliArgumentException extends InvalidCliArgumentException("Empty arguments")

  override def run: ZIO[ZIOAppArgs, Any, ExitCode] = {
    val application = for {
      rawArguments <- getArgs.map(_.toList)
      cliConfig <- parseCliConfig(rawArguments)
      result <- processArguments(cliConfig.command)
        .provide(
          Client.default,
          Scope.default,
          ZLayer.succeed(Printer()),
          ZLayer.fromFunction((client: Client) => ApiClient(client, cliConfig.baseUrl))
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

  private case class CliConfig(
    command: String,
    baseUrl: String
  )

  private def parseCliConfig(
    arguments: List[String]
  ): IO[InvalidCliArgumentException, CliConfig] = {
    val knownServerFlags = Set("--debug", "--prod")
    val serverFlags = arguments.filter(knownServerFlags.contains)
    val uniqueServerFlags = serverFlags.distinct

    if (uniqueServerFlags.size > 1) {
      ZIO.fail(InvalidCliArgumentException("Only one server option can be used: --debug or --prod"))
    } else {
      val baseUrl = uniqueServerFlags.headOption match {
        case Some("--prod") => ApiClient.ProdBaseUrl
        case Some("--debug") => ApiClient.DebugBaseUrl
        case None => ApiClient.DebugBaseUrl
        case Some(other) =>
          return ZIO.fail(InvalidCliArgumentException(s"Unknown server option: $other"))
      }

      val command = arguments.filterNot(knownServerFlags.contains).mkString(" ")
      ZIO.succeed(CliConfig(command = command, baseUrl = baseUrl))
    }
  }

  private def processArguments(arguments: String) = defer {
    val api = ZIO.service[ApiClient].run
    val printer = ZIO.service[Printer].run

    if (arguments.isBlank) {
      ZIO.fail(EmptyCliArgumentException()).run
    }

    val command = arguments match {
      case s"signup $name $email $password" =>
        api.signup(name, email, password).flatMap(printer.print)
      case "login" => api.login().flatMap(printer.print)
      case s"login $email $password" => api.login(email, password).flatMap(printer.print)
      case s"problems" =>
        api.getAuthToken().flatMap(token => api.getProblems(token)).flatMap(printer.print)
      case s"problem $id" =>
        api.getAuthToken().flatMap(token => api.getProblem(id, token)).flatMap(printer.print)
      case s"questions" =>
        api.getAuthToken().flatMap(token => api.getQuestions(token)).flatMap(printer.print)
      case s"questionnaire $id" =>
        api.getAuthToken().flatMap(t => api.getQuestionnaire(id, t)).flatMap(printer.print)
      case s"questionnaires" =>
        api.getAuthToken().flatMap(t => api.getQuestionnaires(t)).flatMap(printer.print)
      case s"unanswered $questionnaireId" =>
        api
          .getAuthToken()
          .flatMap(t => api.getUnanswered(questionnaireId, t))
          .flatMap(printer.print)
      case s"answer $times $questionnaireId" =>
        api
          .getAuthToken()
          .flatMap(t => answerNTimes(api, questionnaireId, times.toInt, t))
          .flatMap(printer.print)
      case s"answer $questionnaireId" =>
        api.getAuthToken().flatMap(t => answer(api, questionnaireId, t)).flatMap(printer.print)
      case "create-debug-users" =>
        createDebugUsers(api, printer)
      case "help" =>
        Console.printLine(HelpText)
      case _ =>
        ZIO.fail(InvalidCliArgumentException(s"Illegal arguments: $arguments"))
    }

    command.run

    ExitCode.success
  }

  private def createDebugUsers(api: ApiClient, printer: Printer): ZIO[Scope, Throwable, Unit] =
    defer {
      val envValues = readDotEnv().run
      val users = parseCsv(envValues.get("DEBUG_USERS"))
      val passwords = parseCsv(envValues.get("DEBUG_PASSWORDS"))

      if (users.isEmpty) {
        ZIO.fail(InvalidCliArgumentException("DEBUG_USERS is missing or empty")).run
      }

      if (passwords.isEmpty) {
        ZIO.fail(InvalidCliArgumentException("DEBUG_PASSWORDS is missing or empty")).run
      }

      if (users.size != passwords.size) {
        ZIO
          .fail(
            InvalidCliArgumentException(
              s"DEBUG_USERS count (${users.size}) doesn't match DEBUG_PASSWORDS count (${passwords.size})"
            )
          )
          .run
      }

      ZIO
        .foreachDiscard(users.zip(passwords)) { case (email, password) =>
          val name = deriveName(email)

          Console.printLine(s"Creating debug user: $email").orDie *>
            api.signup(name = name, email = email, password = password).flatMap(printer.print)
        }
        .run
    }

  private def deriveName(email: String): String =
    email.takeWhile(_ != '@').trim match {
      case value if value.nonEmpty => value
      case _ => email
    }

  private def parseCsv(value: Option[String]): List[String] =
    value.toList
      .flatMap(_.split(","))
      .map(_.trim)
      .filter(_.nonEmpty)

  private def readDotEnv(): ZIO[Any, Throwable, Map[String, String]] =
    ZIO.attempt {
      val envPath = resolveEnvPath()
      val lines = Files.readAllLines(envPath, StandardCharsets.UTF_8).asScala.toList

      lines
        .map(_.trim)
        .filter(line => line.nonEmpty && !line.startsWith("#"))
        .flatMap { line =>
          val separatorIndex = line.indexOf('=')

          if (separatorIndex <= 0) {
            None
          } else {
            val key = line.substring(0, separatorIndex).trim
            val rawValue = line.substring(separatorIndex + 1).trim
            val value =
              if (
                (rawValue.startsWith("\"") && rawValue.endsWith("\"")) ||
                (rawValue.startsWith("'") && rawValue.endsWith("'"))
              ) rawValue.substring(1, rawValue.length - 1)
              else rawValue

            Some(key -> value)
          }
        }
        .toMap
    }

  private def resolveEnvPath(): Path = {
    val candidates = List(
      Paths.get(".env"),
      Paths.get("..", ".env")
    )

    candidates
      .find(path => Files.exists(path) && Files.isRegularFile(path))
      .getOrElse(
        throw InvalidCliArgumentException("Unable to find .env in current or parent directory")
      )
  }

  private def answer(
    api: ApiClient,
    questionnaireId: String,
    authToken: String
  ): ApiResponse = defer {
    val questionnaire = api.getQuestionnaireItem(questionnaireId, authToken).run

    val answeredIds = questionnaire.answers
      .filter(answer => answer.answer == 1 || answer.answer == -1)
      .map(_.id)
      .toSet

    val notAnsweredIds = questionnaire.questions
      .filter(q => !answeredIds.contains(q.id))
      .map(_.id)

    api
      .postAnswer(
        questionnaireId = questionnaireId,
        questionId = notAnsweredIds.head,
        answer = 1,
        authToken = authToken
      )
      .run
  }

  private def answerNTimes(
    api: ApiClient,
    questionnaireId: String,
    times: Int,
    authToken: String
  ): ApiResponse = defer {
    val questionnaire = api.getQuestionnaireItem(questionnaireId, authToken).run

    val answeredIds = questionnaire.answers
      .filter(answer => answer.answer == 1 || answer.answer == -1)
      .map(_.id)
      .toSet

    val notAnsweredIds = questionnaire.questions
      .filter(q => !answeredIds.contains(q.id))
      .map(_.id)
      .take(times)

    val responses = ZIO.collectAll {
      notAnsweredIds.map { questionId =>
        api.postAnswer(
          questionnaireId = questionnaireId,
          questionId = questionId,
          answer = 1,
          authToken = authToken
        )
      }
    }.run

    responses.last
  }
}
