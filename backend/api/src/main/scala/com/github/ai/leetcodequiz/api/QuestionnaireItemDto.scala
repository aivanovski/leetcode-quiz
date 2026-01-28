package com.github.ai.leetcodequiz.api

import zio.json.{JsonDecoder, JsonEncoder}

case class QuestionnaireItemDto(
  id: String,
  isComplete: Boolean,
  questions: List[QuestionItemDto],
  answers: List[QuestionAnswerDto],
  stats: QuestionnaireStatsDto
) derives JsonEncoder,
      JsonDecoder
