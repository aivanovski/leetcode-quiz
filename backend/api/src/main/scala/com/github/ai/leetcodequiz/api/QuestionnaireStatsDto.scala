package com.github.ai.leetcodequiz.api

import zio.json.{JsonDecoder, JsonEncoder}

case class QuestionnaireStatsDto(
  totalQuestions: Int,
  answered: Int,
  notAnswered: Int
) derives JsonEncoder,
      JsonDecoder
