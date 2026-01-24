package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.db.model.{QuestionEntity, QuestionUid, QuestionnaireUid}
import com.github.ai.leetcodequiz.data.db.repository.{QuestionRepository, QuestionnaireRepository}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.direct.*
import zio.{IO, ZIO}

import java.util.Random
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

class SelectNextQuestionsUseCase(
  private val getRemainedQuestionsUseCase: GetRemainedQuestionsUseCase,
  private val questionnaireRepository: QuestionnaireRepository,
  private val questionRepository: QuestionRepository
) {

  def selectNextQuestions(
    questionnaireUid: Option[QuestionnaireUid],
    numberOfQuestions: Int = 5
  ): IO[DomainError, List[QuestionUid]] = defer {
    val questions = if (questionnaireUid.isDefined) {
      val questionnaire = questionnaireRepository.getByUid(questionnaireUid.get).run
      val alreadySelected = questionnaire.nextQuestions.toSet

      val remainedQuestions =
        getRemainedQuestionsUseCase.getRemainedQuestions(questionnaireUid.get).run

      remainedQuestions.filter(q => !alreadySelected.contains(q.uid))
    } else {
      questionRepository.getAll().run
    }

    chooseQuestions(
      numberOfQuestions = numberOfQuestions,
      questions = questions
    ).map(_.uid)
  }

  private def chooseQuestions(
    numberOfQuestions: Int,
    questions: List[QuestionEntity]
  ): List[QuestionEntity] = {
    val random = Random()

    val buffer = ArrayBuffer[QuestionEntity]()
    buffer.addAll(questions)

    val result = ListBuffer[QuestionEntity]()

    while (result.sizeIs < numberOfQuestions && buffer.nonEmpty) {
      val index = random.nextInt(buffer.size)
      result.addOne(buffer.remove(index))
    }

    result.toList
  }
}
