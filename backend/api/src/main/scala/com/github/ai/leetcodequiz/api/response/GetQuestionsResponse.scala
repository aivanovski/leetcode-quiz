package com.github.ai.leetcodequiz.api.response

import com.github.ai.leetcodequiz.api.QuestionItemDto
import zio.json.{JsonDecoder, JsonEncoder}

case class GetQuestionsResponse(
  questions: List[QuestionItemDto]
) derives JsonEncoder,
      JsonDecoder
