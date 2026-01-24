package com.github.ai.leetcodequiz.presentation.controllers

import com.github.ai.leetcodequiz.api.{ProblemItemDto, ProblemsItemDto}
import com.github.ai.leetcodequiz.api.response.{GetProblemResponse, GetProblemsResponse}
import com.github.ai.leetcodequiz.data.db.model.ProblemId
import com.github.ai.leetcodequiz.data.db.repository.ProblemRepository
import com.github.ai.leetcodequiz.data.json.JsonSerializer
import com.github.ai.leetcodequiz.entity.Problem
import com.github.ai.leetcodequiz.utils.{parseIdFromUrl, toProblemItemDto, toProblemsItemDto}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*
import zio.http.{Request, Response}

class ProblemController(
  private val problemRepository: ProblemRepository,
  private val jsonSerializer: JsonSerializer
) {

  def getProblems(): IO[DomainError, Response] = defer {
    val problems = problemRepository.getAll().run
    val dtos = problems.map(p => toProblemsItemDto(p))
    Response.json(jsonSerializer.serialize(GetProblemsResponse(dtos)))
  }

  def getProblem(
    request: Request
  ): IO[DomainError, Response] = defer {
    val id = request.parseIdFromUrl().run
    val problem = problemRepository.getById(id = ProblemId(id)).run

    if (problem.isEmpty) {
      ZIO.fail(DomainError(s"Failed to find entity by id: $id")).run
    }

    val dto = toProblemItemDto(problem.get)
    Response.json(jsonSerializer.serialize(GetProblemResponse(dto)))
  }
}
