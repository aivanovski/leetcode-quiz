package com.github.ai.leetcodequiz.api.response

import com.github.ai.leetcodequiz.api.QuestionnairesItemDto
import zio.json.{JsonDecoder, JsonEncoder}

case class PostSubmissionResponse(
  questionnaire: QuestionnairesItemDto
) derives JsonEncoder,
      JsonDecoder
