package com.github.ai.leetcodequiz.presentation.routes

import com.github.ai.leetcodequiz.domain.authentication.AuthHandler.authHandler
import com.github.ai.leetcodequiz.presentation.controllers.QuestionnaireController
import com.github.ai.leetcodequiz.utils.transformError
import zio.ZIO
import zio.http.{Method, Request, Routes, handler, string}

object QuestionnaireRoutes {

  def routes() = Routes(
    Method.GET / "api" / "questionnaire" -> handler {
      ZIO.serviceWithZIO[QuestionnaireController](_.getQuestionnaires().transformError())
    } @@ authHandler,
    Method.GET / "api" / "questionnaire" / string("questionnaireId") -> handler {
      (request: Request) =>
        ZIO.serviceWithZIO[QuestionnaireController](_.getQuestionnaire(request).transformError())
    } @@ authHandler,
    Method.POST / "api" / "questionnaire" / string("questionnaireId") -> handler {
      (request: Request) =>
        ZIO.serviceWithZIO[QuestionnaireController](_.postSubmission(request).transformError())
    } @@ authHandler
  )
}
