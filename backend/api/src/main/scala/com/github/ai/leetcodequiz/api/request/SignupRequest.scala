package com.github.ai.leetcodequiz.api.request

import zio.json.{JsonDecoder, JsonEncoder}

case class SignupRequest(
  name: String,
  email: String,
  password: String
) derives JsonEncoder, JsonDecoder
