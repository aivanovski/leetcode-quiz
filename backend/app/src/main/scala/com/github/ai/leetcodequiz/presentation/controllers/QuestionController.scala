package com.github.ai.leetcodequiz.presentation.controllers

import com.github.ai.leetcodequiz.api.QuestionDto
import com.github.ai.leetcodequiz.api.response.GetQuestionsResponse
import com.github.ai.leetcodequiz.data.JsonSerializer
import com.github.ai.leetcodequiz.utils.toJavaList
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*
import zio.http.Response

class QuestionController(
  private val jsonSerializer: JsonSerializer
) {
  def getQuestions(): IO[DomainError, Response] =
    defer {
      Response.json(jsonSerializer.serialize(createResponse()))
    }

  private def createResponse(): GetQuestionsResponse = {
    GetQuestionsResponse(
      questions = List(
        QuestionDto(
          id = 1,
          title = "Question N1",
          content = "Some content",
          hints = List("hint", "hint 2").toJavaList(),
          likes = 1,
          dislikes = 2,
          categoryTitle = "",
          difficulty = "",
          url = "http://test.com"
        )
      ).toJavaList()
    )
  }
}
