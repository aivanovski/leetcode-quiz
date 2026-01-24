package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.db.model.{
  QuestionUid,
  QuestionnaireEntity,
  QuestionnaireUid,
  SubmissionEntity,
  SubmissionUid
}
import com.github.ai.leetcodequiz.data.db.repository.{
  QuestionRepository,
  QuestionnaireRepository,
  SubmissionRepository
}
import com.github.ai.leetcodequiz.entity.Questionnaire
import com.github.ai.leetcodequiz.entity.exception.DomainError

import java.util.Random
import zio.{IO, ZIO}
import zio.direct.*

import java.util.UUID

class SubmitQuestionAnswerUseCase(
  private val getRemainedQuestionsUseCase: GetRemainedQuestionsUseCase,
  private val selectNextQuestionsUseCase: SelectNextQuestionsUseCase,
  private val questionRepository: QuestionRepository,
  private val questionnaireRepository: QuestionnaireRepository,
  private val submissionRepository: SubmissionRepository
) {

  def submitAnswer(
    questionnaireUid: QuestionnaireUid,
    questionUid: QuestionUid,
    answer: Int
  ): IO[DomainError, Questionnaire] = defer {
    val questionnaire = questionnaireRepository.getByUid(questionnaireUid).run
    val remainedQuestions = getRemainedQuestionsUseCase.getRemainedQuestions(questionnaireUid).run
    val remainedQuestionUids = remainedQuestions.map(_.uid)

    if (questionnaire.isComplete) {
      ZIO.fail(DomainError(s"Questionnaire is already complete")).run
    }

    if (!remainedQuestionUids.contains(questionUid)) {
      ZIO.fail(DomainError(s"Question not found: $questionUid")).run
    }

    if (answer != 1 && answer != 1) {
      ZIO.fail(DomainError(s"Invalid answer: $answer")).run
    }

    val isLastQuestion = (remainedQuestions.size == 1)

    val submission = SubmissionEntity(
      uid = SubmissionUid(UUID.randomUUID()),
      questionnaireUid = questionnaireUid,
      questionUid = questionUid,
      answer = answer
    )

    submissionRepository.add(submission).run

    if (isLastQuestion) {
      // questionnaire if finished
      questionnaireRepository
        .update(
          questionnaire.copy(
            isComplete = true,
            nextQuestions = List.empty
          )
        )
        .run
    } else {
      val nextQuestions = selectNextQuestionsUseCase.selectNextQuestions(questionnaireUid).run

      questionnaireRepository
        .update(
          questionnaire.copy(
            nextQuestions = nextQuestions
          )
        )
        .run
    }

    questionnaireRepository.getByUid(questionnaireUid).run
  }
}
