package com.github.ai.leetcodequiz.api

import zio.json.{JsonDecoder, JsonEncoder}

case class QuestionnaireItemDto(
  id: String,
  isComplete: Boolean,
  nextQuestions: List[QuestionItemDto]
) derives JsonEncoder, JsonDecoder
