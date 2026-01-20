package com.github.ai.leetcodequiz.apisc

import zio.json.{JsonDecoder, JsonEncoder}

case class UserDto(
  name: String,
  email: String
) derives JsonEncoder, JsonDecoder