package com.github.ai.leetcodequiz.apisc.response

import com.github.ai.leetcodequiz.apisc.UserDto
import zio.json.{JsonDecoder, JsonEncoder}

case class SignupResponse(
  token: String,
  user: UserDto
) derives JsonEncoder, JsonDecoder
