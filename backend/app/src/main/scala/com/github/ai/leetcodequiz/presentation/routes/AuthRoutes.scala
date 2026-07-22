package com.github.ai.leetcodequiz.presentation.routes

import com.github.ai.leetcodequiz.presentation.controllers.AuthController
import com.github.ai.leetcodequiz.presentation.protoHandler
import zio.http.*

object AuthRoutes {

  def routes() = Routes(
    Method.POST / "api" / "signup" -> protoHandler[AuthController] { (controller, request) =>
      controller.signup(request)
    },
    Method.POST / "api" / "login" -> protoHandler[AuthController] { (controller, request) =>
      controller.login(request)
    },
    Method.POST / "api" / "auth" / "refresh" -> protoHandler[AuthController] {
      (controller, request) => controller.refresh(request)
    }
  )
}
