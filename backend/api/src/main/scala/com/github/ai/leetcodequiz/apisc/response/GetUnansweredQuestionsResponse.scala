package com.github.ai.leetcodequiz.apisc.response

import com.github.ai.leetcodequiz.apisc.QuestionItemDto
import zio.json.{JsonDecoder, JsonEncoder}

case class GetUnansweredQuestionsResponse(
  questions: List[QuestionItemDto]
) derives JsonEncoder, JsonDecoder
