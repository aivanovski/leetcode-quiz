package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.db.model.{QuestionEntity, QuestionUid, QuestionnaireUid}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.direct.*
import zio.{IO, ZIO}

import java.util.Random
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

class SelectNextQuestionsUseCase(
  private val getRemainedQuestionsUseCase: GetRemainedQuestionsUseCase
) {

  def selectNextQuestions(
    questionnaireUid: QuestionnaireUid
  ): IO[DomainError, List[QuestionUid]] = defer {
    val remainedQuestions = getRemainedQuestionsUseCase.getRemainedQuestions(questionnaireUid).run

    chooseQuestions(
      numberOfQuestions = 5,
      questions = remainedQuestions
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
