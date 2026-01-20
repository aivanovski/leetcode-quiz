package com.github.ai.leetcodequiz.apisc.response

import com.github.ai.leetcodequiz.apisc.QuestionnaireItemDto
import zio.json.{JsonDecoder, JsonEncoder}

case class GetQuestionnairesResponse(
  questionnaires: List[QuestionnaireItemDto]
) derives JsonEncoder, JsonDecoder
