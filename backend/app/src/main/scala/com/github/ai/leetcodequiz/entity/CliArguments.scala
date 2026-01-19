package com.github.ai.leetcodequiz.entity

import com.github.ai.leetcodequiz.entity.HttpProtocol.{HTTP, HTTPS}

case class CliArguments(
  environment: AppEnvironment,
  protocol: HttpProtocol
) {

  def getPort(): Int = {
    protocol match
      case HTTP => 8080
      case HTTPS => 8443
  }
}
