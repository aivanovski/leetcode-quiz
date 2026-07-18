package com.github.ai.leetcodequiz.presentation.routes

import com.github.ai.leetcodequiz.domain.authentication.AuthHandler.authHandler
import com.github.ai.leetcodequiz.presentation.controllers.{
  AnswerController,
  QuestionnaireController
}
import com.github.ai.leetcodequiz.presentation.protoHandler
import zio.http.{Method, Routes, string}

object QuestionnaireRoutes {

  def routes() = Routes(
    Method.GET / "api" / "questionnaire" -> protoHandler[QuestionnaireController] {
      (controller, _) => controller.getQuestionnaires()
    } @@ authHandler,
    Method.GET / "api" / "questionnaire" / string("questionnaireId") -> protoHandler[
      QuestionnaireController
    ] { (controller, request) =>
      controller.getQuestionnaire(request)
    } @@ authHandler,
    Method.POST / "api" / "questionnaire" / string("questionnaireId") -> protoHandler[
      AnswerController
    ] { (controller, request) =>
      controller.postAnswer(request)
    } @@ authHandler
  )
}
