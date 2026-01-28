package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.db.model.QuestionnaireUid
import com.github.ai.leetcodequiz.data.db.repository.{QuestionRepository, QuestionnaireRepository}
import com.github.ai.leetcodequiz.entity.{Answer, QuestionnaireStats}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

class GetQuestionnaireStatsUseCase(
  private val questionnaireRepository: QuestionnaireRepository,
  private val questionRepository: QuestionRepository
) {

  def getStats(questionnaireUid: QuestionnaireUid): IO[DomainError, QuestionnaireStats] = defer {
    val questionnaire = questionnaireRepository.getByUid(questionnaireUid).run
    val allQuestions = questionRepository.getAll().run

    val answeredQuestionUids = questionnaire.answers
      .filter(answer => answer.answer == 1 || answer.answer == -1)
      .map(_.uid)
      .toSet

    val questionUidToAnswerMap =
      questionnaire.answers.map(answer => (answer.uid, answer.answer)).toMap

    val answeredQuestions = allQuestions.filter(q => answeredQuestionUids.contains(q.uid))
    val notAnsweredQuestions = allQuestions.filter(q => !answeredQuestionUids.contains(q.uid))

    val answers = questionnaire.questions
      .filter(uid => questionUidToAnswerMap.contains(uid))
      .map { uid =>
        val answer = questionUidToAnswerMap.getOrElse(uid, 0)
        Answer(uid, answer)
      }

    QuestionnaireStats(
      answeredQuestions = answeredQuestions.size,
      notAnsweredQuestions = notAnsweredQuestions.size,
      answeredPositively = answers.count(answer => answer.answer == 1),
      answeredNegatively = answers.count(answer => answer.answer == -1)
    )
  }
}
