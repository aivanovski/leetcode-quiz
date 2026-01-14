package com.github.ai.leetcodequiz.client

import com.google.gson.GsonBuilder
import zio.*
import zio.http.*

class ApiClient(
  private val client: Client
) {

  type ApiResponse = ZIO[Scope, Throwable, Response]

  private val gson = GsonBuilder().setPrettyPrinting().create()
  private val DefaultPassword = "abc123"
  private val baseUrl = "https://127.0.0.1:8443"

  def getQuestions(): ApiResponse = {
    client.request(
      Request.get(
        path = s"$baseUrl/question"
      )
    )
  }
}

object Groups {
  val TripToDisneyLand = "00000000-0000-0000-0000-b00000000001"
}
