package com.github.ai.leetcodequiz.apisc

import zio.json.{JsonDecoder, JsonEncoder}

case class ErrorMessageDto(
  message: String,
  exception: String,
  stacktraceBase64: String,
  stacktraceLines: List[String]
) derives JsonEncoder, JsonDecoder
