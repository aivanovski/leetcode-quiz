package com.github.ai.leetcodequiz.apisc.response

import com.github.ai.leetcodequiz.apisc.ProblemsItemDto
import zio.json.{JsonDecoder, JsonEncoder}

case class GetProblemsResponse(
  problems: List[ProblemsItemDto]
) derives JsonEncoder, JsonDecoder
