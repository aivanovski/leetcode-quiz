package com.github.ai.leetcodequiz.domain.authentication

import com.github.ai.leetcodequiz.domain.authentication.JwtTokeService
import com.github.ai.leetcodequiz.utils.transformError
import zio.ZIO
import zio.http.HandlerAspect

object AuthHandler {

  val authHandler = HandlerAspect.bearerAuthZIO { token =>
    ZIO.serviceWithZIO[JwtTokeService] { authService =>
      authService
        .validateToken(token.stringValue)
        .map(_ => true)
        .transformError()
    }
  }
}
