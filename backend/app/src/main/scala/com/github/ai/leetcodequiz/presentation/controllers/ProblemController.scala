package com.github.ai.leetcodequiz.presentation.controllers

import com.github.ai.leetcodequiz.api.{ProblemItemDto, ProblemsItemDto}
import com.github.ai.leetcodequiz.api.response.{GetProblemResponse, GetProblemsResponse}
import com.github.ai.leetcodequiz.data.db.model.ProblemId
import com.github.ai.leetcodequiz.data.db.repository.ProblemRepository
import com.github.ai.leetcodequiz.data.json.JsonSerializer
import com.github.ai.leetcodequiz.entity.Problem
import com.github.ai.leetcodequiz.utils.toJavaList
import com.github.ai.leetcodequiz.utils.parseIdFromUrl
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

    Response.json(jsonSerializer.serialize(createProblemsResponse(problems)))
  }

  def getProblem(
    request: Request
  ): IO[DomainError, Response] = defer {
    val id = parseIdFromUrl(request).run
    val problem = problemRepository.getById(id = ProblemId(id)).run

    if (problem.isEmpty) {
      ZIO.fail(DomainError(s"Failed to find entity by id: $id")).run
    }

    Response.json(jsonSerializer.serialize(createProblemResponse(problem.get)))
  }

  private def createProblemResponse(problem: Problem): GetProblemResponse = {
    GetProblemResponse(
      ProblemItemDto(
        id = problem.id.toString.toInt,
        title = problem.title,
        content = problem.content,
        hints = problem.hints.toJavaList(),
        categoryTitle = problem.category,
        difficulty = problem.difficulty.toString,
        url = problem.url,
        likes = problem.likes,
        dislikes = problem.dislikes
      )
    )
  }

  private def createProblemsResponse(problems: List[Problem]): GetProblemsResponse = {
    GetProblemsResponse(
      problems
        .map { problem =>
          ProblemsItemDto(
            id = problem.id.toString.toInt,
            title = problem.title,
            categoryTitle = problem.category,
            difficulty = problem.difficulty.toString,
            url = problem.url,
            likes = problem.likes,
            dislikes = problem.dislikes
          )
        }
        .toJavaList()
    )
  }
}
