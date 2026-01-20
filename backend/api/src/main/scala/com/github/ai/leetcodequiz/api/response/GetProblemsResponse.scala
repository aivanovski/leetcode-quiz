package com.github.ai.leetcodequiz.api.response

import com.github.ai.leetcodequiz.api.ProblemsItemDto
import zio.json.{JsonDecoder, JsonEncoder}

case class GetProblemsResponse(
  problems: List[ProblemsItemDto]
) derives JsonEncoder, JsonDecoder
