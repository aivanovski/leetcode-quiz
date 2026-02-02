package com.github.ai.leetcodequiz.api

import zio.json.{JsonDecoder, JsonEncoder}

case class SolutionItemDto(
  contentBase64: String
) derives JsonEncoder,
      JsonDecoder
