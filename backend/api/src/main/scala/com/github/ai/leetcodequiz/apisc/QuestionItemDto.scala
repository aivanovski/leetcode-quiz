package com.github.ai.leetcodequiz.apisc

import zio.json.{JsonDecoder, JsonEncoder}

case class QuestionItemDto(
  id: String,
  problemId: Int,
  problemTitle: String,
  question: String,
  complexity: String
) derives JsonEncoder, JsonDecoder
