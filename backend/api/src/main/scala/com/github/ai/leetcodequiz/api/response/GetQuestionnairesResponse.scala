package com.github.ai.leetcodequiz.api.response

import com.github.ai.leetcodequiz.api.QuestionnairesItemDto
import zio.json.{JsonDecoder, JsonEncoder}

case class GetQuestionnairesResponse(
  questionnaires: List[QuestionnairesItemDto]
) derives JsonEncoder,
      JsonDecoder
