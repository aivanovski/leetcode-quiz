package com.github.ai.leetcodequiz.presentation.controllers

import com.github.ai.leetcodequiz.api.QuestionItemDto
import com.github.ai.leetcodequiz.api.response.GetUnansweredQuestionsResponse
import com.github.ai.leetcodequiz.data.db.model.{ProblemId, QuestionEntity, QuestionnaireUid}
import com.github.ai.leetcodequiz.data.db.repository.{
  ProblemRepository,
  QuestionRepository,
  QuestionnaireRepository,
  SubmissionRepository
}
import com.github.ai.leetcodequiz.data.json.JsonSerializer
import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.utils.{getLastUrlParameter, parseUid}
import zio.direct.*
import zio.http.{Request, Response}
import zio.IO

class UnasweredQuestionnareController(
  private val questionnaireRepository: QuestionnaireRepository,
  private val questionRepository: QuestionRepository,
  private val submissionRepository: SubmissionRepository,
  private val problemRepository: ProblemRepository,
  private val jsonSerializer: JsonSerializer
) {

  def getUnanswered(
    request: Request
  ): IO[DomainError, Response] = defer {
    val questionnaireUid = request
      .getLastUrlParameter()
      .flatMap(str => str.parseUid())
      .map(QuestionnaireUid(_))
      .run

    questionnaireRepository.getByUid(questionnaireUid).run

    val submissions = submissionRepository.getByQuestionnaireUid(questionnaireUid).run
    val answeredQuestionUids = submissions.map(_.questionUid).toSet

    val questions = questionRepository.getAll().run
    val problems = problemRepository.getAll().run
    val problemIdToTitleMap = problems.map(problem => (problem.id, problem.title)).toMap

    val unanswered = questions.filter(q => !answeredQuestionUids.contains(q.uid))
    val response = createResposne(unanswered, problemIdToTitleMap)

    Response.json(jsonSerializer.serialize(response))
  }

  private def createResposne(
    questions: List[QuestionEntity],
    problemIdToTitleMap: Map[ProblemId, String]
  ): GetUnansweredQuestionsResponse = {
    GetUnansweredQuestionsResponse(
      questions
        .map { question =>
          QuestionItemDto(
            id = question.uid.toString,
            problemId = question.problemId.toString.toInt,
            problemTitle = problemIdToTitleMap.getOrElse(question.problemId, ""),
            question = question.question,
            complexity = question.complexity
          )
        }
    )
  }
}
