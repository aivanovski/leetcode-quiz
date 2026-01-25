package com.github.ai.leetcodequiz.domain.authentication

import com.github.ai.leetcodequiz.domain.authentication.AuthService
import com.github.ai.leetcodequiz.utils.toDomainResponse
import com.github.ai.leetcodequiz.entity.exception.{InvalidAuthTokenError, MissingAuthTokenError}
import zio.ZIO
import zio.http.{Handler, HandlerAspect, Header, Request}
import zio.direct.*

object AuthHandler {

  val authHandler: HandlerAspect[AuthService, Unit] =
    HandlerAspect.interceptIncomingHandler(Handler.fromFunctionZIO[Request] { request =>
      handleAuth(request)
        .mapError(error => error.toDomainResponse())
    })

  private def handleAuth(request: Request) = defer {
    val token = request.header(Header.Authorization) match {
      case Some(Header.Authorization.Bearer(token)) => ZIO.succeed(token.value.asString).run
      case Some(_) => ZIO.fail(InvalidAuthTokenError()).run
      case None => ZIO.fail(MissingAuthTokenError()).run
    }

    val authService = ZIO.service[AuthService].run

    authService
      .validateToken(token)
      .mapError(error => InvalidAuthTokenError(cause = Some(error)))
      .run

    (request, ())
  }
}
