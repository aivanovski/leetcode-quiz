package com.github.ai.leetcodequiz.presentation.routes

import com.github.ai.leetcodequiz.presentation.controllers.AuthController
import com.github.ai.leetcodequiz.utils.transformError
import zio.ZIO
import zio.http.*

object AuthRoutes {

  def routes() = Routes(
    Method.POST / "api" / "signup" -> handler { (request: Request) =>
      ZIO.serviceWithZIO[AuthController](_.signup(request).transformError())
    },
    Method.POST / "api" / "login" -> handler { (request: Request) =>
      ZIO.serviceWithZIO[AuthController](_.login(request).transformError())
    }
  )
}
