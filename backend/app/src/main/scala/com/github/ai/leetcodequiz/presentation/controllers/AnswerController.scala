package com.github.ai.leetcodequiz.presentation.controllers

import com.github.ai.leetcodequiz.api.request.PostSubmissionRequest
import com.github.ai.leetcodequiz.api.response.PostSubmissionResponse
import com.github.ai.leetcodequiz.data.db.model.{QuestionUid, QuestionnaireUid}
import com.github.ai.leetcodequiz.data.db.repository.{
  ProblemRepository,
  QuestionRepository,
  QuestionnaireRepository
}
import com.github.ai.leetcodequiz.data.json.JsonSerializer
import com.github.ai.leetcodequiz.domain.usecases.{
  CreateNewQuestionnaireUseCase,
  GetQuestionnaireStatsUseCase,
  SubmitQuestionAnswerUseCase
}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.utils.{
  getLastUrlParameter,
  parseUid,
  readBodyAsString,
  toQuestionnaireItemDto
}
import zio.*
import zio.direct.*
import zio.http.{Request, Response}

class AnswerController(
  private val submitAnswerUseCase: SubmitQuestionAnswerUseCase,
  private val getStatsUseCase: GetQuestionnaireStatsUseCase,
  private val questionRepository: QuestionRepository,
  private val jsonSerializer: JsonSerializer
) {

  def postAnswer(
    request: Request
  ): IO[DomainError, Response] = defer {
    val body = request
      .readBodyAsString()
      .flatMap { text => jsonSerializer.deserialize[PostSubmissionRequest](text) }
      .run

    val questionnaireUid = request
      .getLastUrlParameter()
      .flatMap(str => str.parseUid())
      .map(QuestionnaireUid(_))
      .run

    val questionUid = body.questionId.parseUid().map(QuestionUid(_)).run

    val questionnaire = submitAnswerUseCase
      .submitAnswer(
        questionnaireUid = questionnaireUid,
        questionUid = questionUid,
        answer = body.answer
      )
      .run

    val stats = getStatsUseCase.getStats(questionnaireUid).run

    val questionUidToQuestionMap = questionRepository
      .getAll()
      .run
      .map(q => (q.uid, q))
      .toMap

    val response = toQuestionnaireItemDto(
      questionnaire = questionnaire,
      stats = stats,
      questionUidToQuestionMap = questionUidToQuestionMap
    ).run

    Response.json(jsonSerializer.serialize(PostSubmissionResponse(response)))
  }

}
