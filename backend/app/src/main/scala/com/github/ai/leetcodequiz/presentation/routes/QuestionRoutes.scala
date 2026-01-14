package com.github.ai.leetcodequiz.presentation.routes

import com.github.ai.leetcodequiz.utils.toDomainResponse
import com.github.ai.leetcodequiz.presentation.controllers.QuestionController
import zio.ZIO
import zio.http.{Method, Request, Routes, handler}

object QuestionRoutes {

  def routes() = Routes(
    Method.GET / "api" / "question" -> handler { (request: Request) =>
      for {
        controller <- ZIO.service[QuestionController]
        response <- controller.getQuestions().mapError(_.toDomainResponse)
      } yield response
    }
  )
}
