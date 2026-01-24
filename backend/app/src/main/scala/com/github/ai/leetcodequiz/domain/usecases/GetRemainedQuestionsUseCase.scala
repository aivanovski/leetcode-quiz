package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.db.model.{QuestionEntity, QuestionnaireUid}
import com.github.ai.leetcodequiz.data.db.repository.{
  QuestionRepository,
  QuestionnaireRepository,
  SubmissionRepository
}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

class GetRemainedQuestionsUseCase(
  private val questionnaireRepository: QuestionnaireRepository,
  private val questionRepository: QuestionRepository,
  private val submissionRepository: SubmissionRepository
) {

  def getRemainedQuestions(
    questionnaireUid: QuestionnaireUid
  ): IO[DomainError, List[QuestionEntity]] = defer {
    val allQuestions = questionRepository.getAll().run
    if (allQuestions.isEmpty) {
      ZIO.fail(DomainError("No questions to select")).run
    }

    val submissions = submissionRepository.getByQuestionnaireUid(questionnaireUid).run

    val submittedQuestionUids = submissions.map(_.questionUid).toSet
    val remainedQuestions =
      allQuestions.filter(question => !submittedQuestionUids.contains(question.uid))

    remainedQuestions
  }
}
