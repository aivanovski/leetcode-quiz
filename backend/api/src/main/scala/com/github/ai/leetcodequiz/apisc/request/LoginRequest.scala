package com.github.ai.leetcodequiz.apisc.request

import zio.json.{JsonDecoder, JsonEncoder}

case class LoginRequest(
  email: String,
  password: String
) derives JsonEncoder, JsonDecoder
