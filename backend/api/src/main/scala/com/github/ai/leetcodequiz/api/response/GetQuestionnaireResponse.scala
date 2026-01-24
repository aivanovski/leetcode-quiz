package com.github.ai.leetcodequiz.api.response

import com.github.ai.leetcodequiz.api.{QuestionnaireItemDto, QuestionnairesItemDto}
import zio.json.{JsonDecoder, JsonEncoder}

case class GetQuestionnaireResponse(
  questionnaire: QuestionnaireItemDto
) derives JsonEncoder,
      JsonDecoder
