package com.github.ai.leetcodequiz.presentation.controllers

import com.github.ai.leetcodequiz.api.PostSubmissionRequest
import com.github.ai.leetcodequiz.api.PostSubmissionResponse
import com.github.ai.leetcodequiz.data.db.model.{QuestionUid, QuestionnaireUid}
import com.github.ai.leetcodequiz.data.db.repository.QuestionRepository
import com.github.ai.leetcodequiz.data.protobuf.ProtobufSerializer
import com.github.ai.leetcodequiz.domain.usecases.{
  GetQuestionnaireStatsUseCase,
  SubmitQuestionAnswerUseCase
}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.utils.{
  getLastUrlParameter,
  parseUid,
  readBodyAsBytes,
  toQuestionnaireItemDto
}
import zio.*
import zio.direct.*
import zio.http.Request

class AnswerController(
  private val submitAnswerUseCase: SubmitQuestionAnswerUseCase,
  private val getStatsUseCase: GetQuestionnaireStatsUseCase,
  private val questionRepository: QuestionRepository,
  private val protobufSerializer: ProtobufSerializer
) {

  def postAnswer(
    request: Request
  ): IO[DomainError, PostSubmissionResponse] = defer {
    val body = request
      .readBodyAsBytes()
      .flatMap { bytes => protobufSerializer.deserialize[PostSubmissionRequest](bytes) }
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

    PostSubmissionResponse(response)
  }
}
