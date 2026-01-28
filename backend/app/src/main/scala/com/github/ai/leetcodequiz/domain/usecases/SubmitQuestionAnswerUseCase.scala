package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.db.model.{QuestionUid, QuestionnaireUid}
import com.github.ai.leetcodequiz.data.db.repository.{QuestionRepository, QuestionnaireRepository}
import com.github.ai.leetcodequiz.entity.Questionnaire
import com.github.ai.leetcodequiz.entity.exception.DomainError

import zio.{IO, ZIO}
import zio.direct.*

class SubmitQuestionAnswerUseCase(
  private val getRemainedQuestionsUseCase: GetRemainedQuestionsUseCase,
  private val selectNextQuestionsUseCase: SelectNextQuestionsUseCase,
  private val questionRepository: QuestionRepository,
  private val questionnaireRepository: QuestionnaireRepository
) {

  def submitAnswer(
    questionnaireUid: QuestionnaireUid,
    questionUid: QuestionUid,
    answer: Int
  ): IO[DomainError, Questionnaire] = defer {
    val questionnaire = questionnaireRepository.getByUid(questionnaireUid).run
    val allQuestions = questionRepository.getAll().run
    val remainedQuestions = getRemainedQuestionsUseCase.getRemainedQuestions(questionnaireUid).run

    val remainedQuestionUids = remainedQuestions.map(_.uid).toSet
    val selectedQuestionUids = questionnaire.questions.toSet

    if (questionnaire.isComplete) {
      ZIO.fail(DomainError(s"Questionnaire is already complete")).run
    }

    // TODO: check if question is in questionnaire list
    if (!allQuestions.exists(q => q.uid == questionUid)) {
      ZIO.fail(DomainError(s"Question not found: $questionUid")).run
    }

    if (answer != -1 && answer != 1) {
      ZIO.fail(DomainError(s"Invalid answer: $answer")).run
    }

    val isLastQuestion = (remainedQuestions.size == 1 && remainedQuestions.head.uid == questionUid)

    questionnaireRepository
      .addOrUpdateAnswer(
        questionnaireUid = questionnaireUid,
        questionUid = questionUid,
        answer = answer
      )
      .run

    if (isLastQuestion) {
      // questionnaire is finished
      questionnaireRepository
        .update(
          questionnaire.copy(
            isComplete = true
          )
        )
        .run
    }

    questionnaireRepository.getByUid(questionnaireUid).run
  }
}
