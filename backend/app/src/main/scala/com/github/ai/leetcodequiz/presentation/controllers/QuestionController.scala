package com.github.ai.leetcodequiz.presentation.controllers

import com.github.ai.leetcodequiz.api.GetQuestionsResponse
import com.github.ai.leetcodequiz.data.db.repository.{ProblemRepository, QuestionRepository}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.utils.toQuestionItemDto
import zio.*
import zio.direct.*

class QuestionController(
  private val problemRepository: ProblemRepository,
  private val questionRepository: QuestionRepository
) {

  def getQuestions(): IO[DomainError, GetQuestionsResponse] = defer {
    val questions = questionRepository.getAll().run
    val problems = problemRepository.getAll().run

    val problemMap = problems.map(problem => (problem.id, problem)).toMap

    val questionsAndProblems = questions.flatMap { question =>
      problemMap
        .get(question.problemId)
        .map(p => (question, p))
    }

    val questionDtos = questionsAndProblems.map { (question, _) =>
      toQuestionItemDto(question)
    }

    GetQuestionsResponse(questionDtos)
  }
}
