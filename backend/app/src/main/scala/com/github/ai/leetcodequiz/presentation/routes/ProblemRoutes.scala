package com.github.ai.leetcodequiz.presentation.routes

import com.github.ai.leetcodequiz.domain.authentication.AuthHandler.authHandler
import com.github.ai.leetcodequiz.presentation.controllers.ProblemController
import com.github.ai.leetcodequiz.presentation.protoHandler
import zio.http.*

object ProblemRoutes {

  def routes() = Routes(
    Method.GET / "api" / "problem" -> protoHandler[ProblemController] { (controller, _) =>
      controller.getProblems()
    } @@ authHandler,
    Method.GET / "api" / "problem" / string("problemId") -> protoHandler[ProblemController] {
      (controller, request) =>
        controller.getProblem(request)
    } @@ authHandler
  )
}
