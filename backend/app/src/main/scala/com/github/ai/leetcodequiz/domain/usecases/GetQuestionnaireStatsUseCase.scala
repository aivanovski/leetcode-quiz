package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.db.model.QuestionnaireUid
import com.github.ai.leetcodequiz.data.db.repository.{
  QuestionRepository,
  QuestionnaireRepository,
  SubmissionRepository
}
import com.github.ai.leetcodequiz.entity.QuestionnaireStats
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

class GetQuestionnaireStatsUseCase(
  private val questionnaireRepository: QuestionnaireRepository,
  private val questionRepository: QuestionRepository,
  private val submissionRepository: SubmissionRepository
) {

  def getStats(questionnaireUid: QuestionnaireUid): IO[DomainError, QuestionnaireStats] = defer {
    val questionnaire = questionnaireRepository.getByUid(questionnaireUid).run
    val submissions = submissionRepository.getByQuestionnaireUid(questionnaireUid).run
    val allQuestions = questionRepository.getAll().run

    val answeredUids = submissions.map(_.questionUid).toSet

    val answeredQuestions = allQuestions.filter(q => answeredUids.contains(q.uid))
    val notAnsweredQuestions = allQuestions.filter(q => !answeredUids.contains(q.uid))

    QuestionnaireStats(
      answeredQuestions = answeredQuestions.size,
      notAnsweredQuestions = notAnsweredQuestions.size
    )
  }
}
