package com.github.ai.leetcodequiz.client

import com.github.ai.leetcodequiz.api.{
  LoginRequest,
  PostSubmissionRequest,
  QuestionnaireItemDto,
  RefreshTokenRequest,
  ResponseDto,
  SignupRequest
}
import zio.*
import zio.http.*

type ApiResponse = ZIO[Scope, Throwable, Response]

class ApiClient(
  private val client: Client,
  private val baseUrl: String = ApiClient.DebugBaseUrl
) {

  def signup(name: String, email: String, password: String): ApiResponse =
    client.request(
      Request.post(
        path = s"$baseUrl/api/signup",
        body = Body.fromArray(
          SignupRequest(name = name, email = email, password = password).toByteArray
        )
      )
    )

  def getAuthToken(
    email: String = DefaultCredentials.DefaultEmail,
    password: String = DefaultCredentials.DefaultPassword
  ): ZIO[Scope, Throwable, String] =
    login(email, password)
      .flatMap(_.body.asArray)
      .map(bytes => ResponseDto.parseFrom(bytes))
      .map(response => response.getLoginResponse.token)

  def getQuestionnaireItem(
    questionnaireId: String,
    authToken: String
  ): ZIO[Scope, Throwable, QuestionnaireItemDto] =
    getQuestionnaire(id = questionnaireId, authToken = authToken)
      .flatMap(_.body.asArray)
      .map(bytes => ResponseDto.parseFrom(bytes))
      .map(response => response.getQuestionnaireResponse.questionnaire)

  def login(
    email: String = DefaultCredentials.DefaultEmail,
    password: String = DefaultCredentials.DefaultPassword
  ): ApiResponse =
    client.request(
      Request.post(
        path = s"$baseUrl/api/login",
        body = Body.fromArray(createLoginRequest(email = email, password = password))
      )
    )

  def refreshAuthToken(
    refreshToken: String
  ): ApiResponse =
    client.request(
      Request.post(
        path = s"$baseUrl/api/auth/refresh",
        body = Body.fromArray(RefreshTokenRequest(refreshToken = refreshToken).toByteArray)
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
        body = Body.fromArray(requestBody.toByteArray)
      )
    )
  }

  private def createLoginRequest(email: String, password: String): Array[Byte] =
    LoginRequest(email = email, password = password).toByteArray

  object DefaultCredentials {
    val DefaultPassword = "abc123"
    val DefaultEmail = "admin@mail.com"
  }
}

object ApiClient {
  val DebugBaseUrl = "http://127.0.0.1:8080"
  val ProdBaseUrl = "https://leetcode.testswithme.org"
}
