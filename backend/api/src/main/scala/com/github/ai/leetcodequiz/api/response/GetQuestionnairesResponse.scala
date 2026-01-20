package com.github.ai.leetcodequiz.api.response

import com.github.ai.leetcodequiz.api.QuestionnaireItemDto
import zio.json.{JsonDecoder, JsonEncoder}

case class GetQuestionnairesResponse(
  questionnaires: List[QuestionnaireItemDto]
) derives JsonEncoder,
      JsonDecoder
