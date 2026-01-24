package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.db.model.{
  QuestionEntity,
  QuestionUid,
  QuestionnaireEntity,
  QuestionnaireUid
}
import com.github.ai.leetcodequiz.data.db.repository.{QuestionRepository, QuestionnaireRepository}
import com.github.ai.leetcodequiz.entity.Questionnaire
import com.github.ai.leetcodequiz.entity.exception.DomainError

import java.util.{Random, UUID}
import zio.{IO, ZIO}
import zio.direct.*

import java.util
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

class CreateNewQuestionnaireUseCase(
  private val selectQuestionsUseCase: SelectNextQuestionsUseCase,
  private val questionRepository: QuestionRepository,
  private val questionnaireRepository: QuestionnaireRepository
) {

  def shouldCreateNewQuestionnaire(): IO[DomainError, Boolean] = defer {
    val questionnaires = questionnaireRepository.getAll().run

    questionnaires.isEmpty || questionnaires.forall(q => q.isComplete)
  }

  def createNewQuestionnaire(): IO[DomainError, Questionnaire] = defer {
    val allQuestions = questionRepository.getAll().run

    val questionnaireUid = QuestionnaireUid(UUID.randomUUID())
    val nextQuestions = selectQuestionsUseCase.selectNextQuestions(questionnaireUid).run

    questionnaireRepository
      .add(
        Questionnaire(
          uid = questionnaireUid,
          isComplete = false,
          nextQuestions = nextQuestions
        )
      )
      .run
  }

}
