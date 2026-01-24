package com.github.ai.leetcodequiz.api

import zio.json.{JsonDecoder, JsonEncoder}

case class QuestionnairesItemDto(
  id: String,
  isComplete: Boolean,
  nextQuestions: List[QuestionItemDto]
) derives JsonEncoder,
      JsonDecoder
