package com.github.ai.leetcodequiz.presentation.routes

import com.github.ai.leetcodequiz.domain.authentication.AuthHandler.authHandler
import com.github.ai.leetcodequiz.presentation.controllers.UnasweredQuestionnareController
import com.github.ai.leetcodequiz.utils.transformError
import zio.ZIO
import zio.http.{Method, Request, Routes, handler, string}

object UnansweredQuestionnaireRoutes {

  def routes() = Routes(
    Method.GET / "api" / "unanswered" / string("questionnaireId") -> handler { (request: Request) =>
      ZIO.serviceWithZIO[UnasweredQuestionnareController](_.getUnanswered(request).transformError())
    } @@ authHandler
  )
}
