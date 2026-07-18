package com.github.ai.leetcodequiz.presentation.controllers

import com.github.ai.leetcodequiz.api.{GetProblemResponse, GetProblemsResponse}
import com.github.ai.leetcodequiz.data.db.model.ProblemId
import com.github.ai.leetcodequiz.data.db.repository.{
  ProblemRepository,
  QuestionRepository,
  SolutionRepository
}
import com.github.ai.leetcodequiz.data.protobuf.ProtobufSerializer
import com.github.ai.leetcodequiz.utils.{parseIdFromUrl, toProblemItemDto, toProblemsItemDto}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*
import zio.http.{Request, Response}

class ProblemController(
  private val problemRepository: ProblemRepository,
  private val solutionRepository: SolutionRepository,
  private val questionRepository: QuestionRepository,
  private val protobufSerializer: ProtobufSerializer
) {

  def getProblems(): IO[DomainError, GetProblemsResponse] = defer {
    val problems = problemRepository.getAll().run
    val dtos = problems.map(p => toProblemsItemDto(p))
    GetProblemsResponse(dtos)
  }

  def getProblem(
    request: Request
  ): IO[DomainError, GetProblemResponse] = defer {
    val id = request.parseIdFromUrl().map(id => ProblemId(id)).run
    val problem = problemRepository.getById(id = id).run
    val solutions = solutionRepository.findByProblemId(id).run
    val question = questionRepository.findByProblemId(id).run

    if (problem.isEmpty) {
      ZIO.fail(DomainError(s"Failed to find entity by id: $id")).run
    }

    val dto = toProblemItemDto(
      problem = problem.get,
      question = question,
      solutions = solutions
    )

    GetProblemResponse(dto)
  }
}
