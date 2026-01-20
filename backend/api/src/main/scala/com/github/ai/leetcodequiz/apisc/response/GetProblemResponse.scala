package com.github.ai.leetcodequiz.apisc.response

import com.github.ai.leetcodequiz.apisc.ProblemItemDto
import zio.json.{JsonDecoder, JsonEncoder}

case class GetProblemResponse(
  problem: ProblemItemDto
) derives JsonEncoder, JsonDecoder
