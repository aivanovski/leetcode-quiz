package com.github.ai.leetcodequiz.apisc.request

import zio.json.{JsonDecoder, JsonEncoder}

case class SignupRequest(
  name: String,
  email: String,
  password: String
) derives JsonEncoder, JsonDecoder
