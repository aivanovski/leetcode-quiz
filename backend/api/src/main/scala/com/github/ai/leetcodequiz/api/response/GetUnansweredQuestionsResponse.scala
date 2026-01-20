package com.github.ai.leetcodequiz.api.response

import com.github.ai.leetcodequiz.api.QuestionItemDto
import zio.json.{JsonDecoder, JsonEncoder}

case class GetUnansweredQuestionsResponse(
  questions: List[QuestionItemDto]
) derives JsonEncoder, JsonDecoder
