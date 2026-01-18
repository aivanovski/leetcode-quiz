package com.github.ai.leetcodequiz.client

import com.github.ai.leetcodequiz.api.request.PostSubmissionRequest
import com.google.gson.GsonBuilder
import zio.*
import zio.http.*
import zio.direct.*

type ApiResponse = ZIO[Scope, Throwable, Response]

class ApiClient(
  private val client: Client
) {

  val gson = GsonBuilder().setPrettyPrinting().create()
  private val DefaultPassword = "abc123"
  private val baseUrl = "https://127.0.0.1:8443"

  def getProblems(): ApiResponse =
    client.request(
      Request.get(
        path = s"$baseUrl/api/problem"
      )
    )

  def getProblem(id: String): ApiResponse =
    client.request(
      Request.get(
        path = s"$baseUrl/api/problem/$id"
      )
    )

  def getQuestionnaires(): ApiResponse =
    client.request(
      Request.get(
        path = s"$baseUrl/api/questionnaire"
      )
    )

  def getQuestionnaire(id: String): ApiResponse =
    client.request(
      Request.get(
        path = s"$baseUrl/api/questionnaire/$id"
      )
    )

  def postAnswer(
    questionnaireId: String,
    questionId: String,
    answer: Int
  ): ApiResponse = {
    val body = PostSubmissionRequest(
      questionId = questionId,
      answer = answer
    )

    client.request(
      Request.post(
        path = s"$baseUrl/api/questionnaire/$questionnaireId",
        body = Body.fromString(gson.toJson(body))
      )
    )
  }
}

object Groups {
  val TripToDisneyLand = "00000000-0000-0000-0000-b00000000001"
}
