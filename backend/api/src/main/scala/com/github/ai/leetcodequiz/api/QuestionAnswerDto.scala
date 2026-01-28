package com.github.ai.leetcodequiz.api

import zio.json.{JsonDecoder, JsonEncoder}

case class QuestionAnswerDto(
  id: String,
  answer: Int
) derives JsonEncoder,
      JsonDecoder
