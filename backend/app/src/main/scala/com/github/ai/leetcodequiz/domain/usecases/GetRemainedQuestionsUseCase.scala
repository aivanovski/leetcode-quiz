package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.db.model.{QuestionEntity, QuestionnaireUid}
import com.github.ai.leetcodequiz.data.db.repository.{QuestionRepository, QuestionnaireRepository}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

class GetRemainedQuestionsUseCase(
  private val questionnaireRepository: QuestionnaireRepository,
  private val questionRepository: QuestionRepository
) {

  def getRemainedQuestions(
    questionnaireUid: QuestionnaireUid
  ): IO[DomainError, List[QuestionEntity]] = defer {
    val questionnaire = questionnaireRepository.getByUid(questionnaireUid).run
    val allQuestions = questionRepository.getAll().run

    val answeredQuestionUids = questionnaire.answers
      .filter(answer => answer.answer == 1 || answer.answer == -1)
      .map(_.uid)
      .toSet

    val questionnaireQuestionUids = questionnaire.questions.toSet

    val remainedQuestions = allQuestions
      .filter(q => questionnaireQuestionUids.contains(q.uid))
      .filter(q => !answeredQuestionUids.contains(q.uid))

    remainedQuestions
  }
}
