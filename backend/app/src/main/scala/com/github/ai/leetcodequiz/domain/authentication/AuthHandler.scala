package com.github.ai.leetcodequiz.domain.authentication

import com.github.ai.leetcodequiz.data.protobuf.ProtobufSerializer
import com.github.ai.leetcodequiz.domain.authentication.AuthService
import com.github.ai.leetcodequiz.entity.AuthToken
import com.github.ai.leetcodequiz.utils.toDomainResponse
import com.github.ai.leetcodequiz.entity.exception.MissingAuthTokenError
import zio.ZIO
import zio.http.{Handler, HandlerAspect, Header, Request}
import zio.direct.*

object AuthHandler {

  val AuthTokenCookieName = "authToken"
  val RefreshTokenCookieName = "refreshToken"

  val authHandler: HandlerAspect[AuthService & ProtobufSerializer, Unit] =
    HandlerAspect.interceptIncomingHandler(Handler.fromFunctionZIO[Request] { request =>
      defer {
        val serializer = ZIO.service[ProtobufSerializer].run

        handleAuth(request)
          .mapError(error => error.toDomainResponse(serializer))
          .run
      }
    })

  private def handleAuth(request: Request) = defer {
    val cookieToken = request
      .cookie(AuthTokenCookieName)
      .map(cookie => AuthToken(cookie.content))

    val headerToken = request
      .header(Header.Authorization)
      .flatMap {
        case Header.Authorization.Bearer(token) => Some(AuthToken(token.value.asString))
        case _ => None
      }

    if (cookieToken.isEmpty && headerToken.isEmpty) {
      ZIO.fail(MissingAuthTokenError()).run
    }

    val authService = ZIO.service[AuthService].run

    if (headerToken.isDefined) {
      authService
        .validateAuthToken(headerToken.get)
        .run
    } else if (cookieToken.isDefined) {
      authService
        .validateAuthToken(cookieToken.get)
        .run
    }

    (request, ())
  }
}
