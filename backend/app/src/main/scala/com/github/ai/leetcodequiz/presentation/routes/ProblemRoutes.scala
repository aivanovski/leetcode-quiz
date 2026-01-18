package com.github.ai.leetcodequiz.presentation.routes

import com.github.ai.leetcodequiz.utils.transformError
import com.github.ai.leetcodequiz.presentation.controllers.ProblemController
import zio.ZIO
import zio.http.*

object ProblemRoutes {

  def routes() = Routes(
    Method.GET / "api" / "problem" -> handler {
      ZIO.serviceWithZIO[ProblemController](_.getProblems().transformError())
    },
    Method.GET / "api" / "problem" / string("problemId") -> handler { (request: Request) =>
      ZIO.serviceWithZIO[ProblemController](_.getProblem(request).transformError())
    }
  )
}
