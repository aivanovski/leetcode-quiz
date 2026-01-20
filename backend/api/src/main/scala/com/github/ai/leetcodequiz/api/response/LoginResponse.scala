package com.github.ai.leetcodequiz.api.response

import com.github.ai.leetcodequiz.api.UserDto
import zio.json.{JsonDecoder, JsonEncoder}

case class LoginResponse(
  token: String,
  user: UserDto
) derives JsonEncoder, JsonDecoder
