package com.github.ai.leetcodequiz.api.response

import com.github.ai.leetcodequiz.api.ProblemItemDto
import zio.json.{JsonDecoder, JsonEncoder}

case class GetProblemResponse(
  problem: ProblemItemDto
) derives JsonEncoder, JsonDecoder
