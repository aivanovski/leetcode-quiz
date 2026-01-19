package com.github.ai.leetcodequiz.presentation.routes

import com.github.ai.leetcodequiz.domain.authentication.AuthHandler.authHandler
import com.github.ai.leetcodequiz.presentation.controllers.QuestionController
import com.github.ai.leetcodequiz.utils.transformError
import zio.ZIO
import zio.http.*

object QuestionRoutes {

  def routes() = Routes(
    Method.GET / "api" / "question" -> handler {
      ZIO.serviceWithZIO[QuestionController](_.getHints().transformError())
    }
  ) @@ authHandler
}
