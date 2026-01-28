package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.db.model.{QuestionEntity, QuestionUid, QuestionnaireUid}
import com.github.ai.leetcodequiz.data.db.repository.{QuestionRepository, QuestionnaireRepository}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.direct.*
import zio.{IO, ZIO}

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.Random

class SelectNextQuestionsUseCase(
  private val getRemainedQuestionsUseCase: GetRemainedQuestionsUseCase,
  private val questionnaireRepository: QuestionnaireRepository,
  private val questionRepository: QuestionRepository
) {

  def selectNextQuestions(
    questionnaireUid: Option[QuestionnaireUid]
  ): IO[DomainError, List[QuestionUid]] = defer {
    val questions = if (questionnaireUid.isDefined) {
      val questionnaire = questionnaireRepository.getByUid(questionnaireUid.get).run
      val alreadySelected = questionnaire.questions.toSet

      val remainedQuestions =
        getRemainedQuestionsUseCase.getRemainedQuestions(questionnaireUid.get).run

      remainedQuestions.filter(q => !alreadySelected.contains(q.uid))
    } else {
      questionRepository.getAll().run
    }

    Random.shuffle(questions).map(_.uid)
  }
}
