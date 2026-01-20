package com.github.ai.leetcodequiz.client

import com.github.ai.leetcodequiz.api.request.{LoginRequest, PostSubmissionRequest, SignupRequest}
import com.github.ai.leetcodequiz.api.response.{LoginResponse, PostSubmissionResponse}
import zio.*
import zio.http.*
import zio.json.*

type ApiResponse = ZIO[Scope, Throwable, Response]

class ApiClient(
  private val client: Client
) {

  private val baseUrl = "https://127.0.0.1:8443"

  def signup(name: String, email: String, password: String): ApiResponse =
    client.request(
      Request.post(
        path = s"$baseUrl/api/signup",
        body = Body.fromString(
          SignupRequest(
            name = name,
            email = email,
            password = password
          ).toJson
        )
      )
    )

  def getAuthToken(
    email: String = DefaultCredentials.DefaultEmail,
    password: String = DefaultCredentials.DefaultPassword
  ): ZIO[Scope, Throwable, String] =
    login(email, password)
      .flatMap(_.body.asString)
      .flatMap { json =>
        ZIO
          .fromEither(json.fromJson[LoginResponse])
          .mapError(e => Exception(s"Unable to parse json response: $e"))
      }
      .map(response => response.token)

  def getFirstQuestionIdFromQuestionnaire(
    questionnaireId: String,
    authToken: String
  ): ZIO[Scope, Throwable, String] =
    getQuestionnaire(id = questionnaireId, authToken = authToken)
      .flatMap(_.body.asString)
      .flatMap { json =>
        ZIO
          .fromEither(json.fromJson[PostSubmissionResponse])
          .mapError(e => Exception(s"Unable to parse json response: $e"))
      }
      .map(response => response.questionnaire.nextQuestions.collectFirst(_.id).getOrElse(""))

  def login(
    email: String = DefaultCredentials.DefaultEmail,
    password: String = DefaultCredentials.DefaultPassword
  ): ApiResponse =
    client.request(
      Request.post(
        path = s"$baseUrl/api/login",
        body = Body.fromString(createLoginRequest(email = email, password = password))
      )
    )

  def getProblems(
    authToken: String
  ): ApiResponse =
    client.request(
      Request(
        method = Method.GET,
        url = URL.decode(s"$baseUrl/api/problem").toOption.get,
        headers = Headers(Header.Authorization.Bearer(authToken))
      )
    )

  def getProblem(
    id: String,
    authToken: String
  ): ApiResponse =
    client.request(
      Request(
        method = Method.GET,
        url = URL.decode(s"$baseUrl/api/problem/$id").toOption.get,
        headers = Headers(Header.Authorization.Bearer(authToken))
      )
    )

  def getQuestions(
    authToken: String
  ): ApiResponse =
    client.request(
      Request(
        method = Method.GET,
        url = URL.decode(s"$baseUrl/api/question").toOption.get,
        headers = Headers(Header.Authorization.Bearer(authToken))
      )
    )

  def getQuestionnaires(
    authToken: String
  ): ApiResponse =
    client.request(
      Request(
        method = Method.GET,
        url = URL.decode(s"$baseUrl/api/questionnaire").toOption.get,
        headers = Headers(Header.Authorization.Bearer(authToken))
      )
    )

  def getQuestionnaire(
    id: String,
    authToken: String
  ): ApiResponse =
    client.request(
      Request(
        method = Method.GET,
        url = URL.decode(s"$baseUrl/api/questionnaire/$id").toOption.get,
        headers = Headers(Header.Authorization.Bearer(authToken))
      )
    )

  def getUnanswered(
    questionnaireId: String,
    authToken: String
  ): ApiResponse =
    client.request(
      Request(
        method = Method.GET,
        url = URL.decode(s"$baseUrl/api/unanswered/$questionnaireId").toOption.get,
        headers = Headers(Header.Authorization.Bearer(authToken))
      )
    )

  def postAnswer(
    questionnaireId: String,
    questionId: String,
    answer: Int,
    authToken: String
  ): ApiResponse = {
    val requestBody = PostSubmissionRequest(
      questionId = questionId,
      answer = answer
    )

    client.request(
      Request(
        method = Method.POST,
        url = URL.decode(s"$baseUrl/api/questionnaire/$questionnaireId").toOption.get,
        headers = Headers(Header.Authorization.Bearer(authToken)),
        body = Body.fromString(requestBody.toJson)
      )
    )
  }

  private def createLoginRequest(email: String, password: String): String =
    LoginRequest(
      email = email,
      password = password
    ).toJson

  object DefaultCredentials {
    val DefaultPassword = "abc123"
    val DefaultEmail = "admin@mail.com"
  }
}
