package com.github.ai.leetcodequiz.presentation.controllers

import com.github.ai.leetcodequiz.api.QuestionItemDto
import com.github.ai.leetcodequiz.api.response.GetHintsResponse
import com.github.ai.leetcodequiz.data.db.model.QuestionEntity
import com.github.ai.leetcodequiz.data.db.repository.{ProblemRepository, QuestionRepository}
import com.github.ai.leetcodequiz.data.json.JsonSerializer
import com.github.ai.leetcodequiz.entity.Problem
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*
import zio.http.Response

class QuestionController(
  private val problemRepository: ProblemRepository,
  private val questionRepository: QuestionRepository,
  private val jsonSerializer: JsonSerializer
) {

  def getHints(): IO[DomainError, Response] = defer {
    val questions = questionRepository.getAll().run
    val problems = problemRepository.getAll().run

    val problemMap = problems.map(problem => (problem.id, problem)).toMap

    val questionsAndProblems = questions.flatMap { question =>
      problemMap
        .get(question.problemId)
        .map(p => (question, p))
    }

    Response.json(jsonSerializer.serialize(createResponse(questionsAndProblems)))
  }

  private def createResponse(data: List[(QuestionEntity, Problem)]): GetHintsResponse = {
    GetHintsResponse(
      data
        .map { (question, problem) =>
          QuestionItemDto(
            id = question.uid.toString,
            problemId = question.problemId.toString.toInt,
            problemTitle = problem.title,
            question = question.question,
            complexity = question.complexity
          )
        }
    )
  }
}
