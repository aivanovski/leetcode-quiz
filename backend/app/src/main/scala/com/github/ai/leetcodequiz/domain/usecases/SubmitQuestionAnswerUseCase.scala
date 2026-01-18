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
import com.github.ai.leetcodequiz.entity.exception.DomainError

import java.util.Random
import zio.{IO, ZIO}
import zio.direct.*

import java.util.UUID

class SubmitQuestionAnswerUseCase(
  private val questionRepository: QuestionRepository,
  private val questionnaireRepository: QuestionnaireRepository,
  private val submissionRepository: SubmissionRepository
) {

  def submitAnswer(
    questionnaireUid: QuestionnaireUid,
    questionUid: QuestionUid,
    answer: Int
  ): IO[DomainError, QuestionnaireEntity] = defer {
    val questionnaire = questionnaireRepository.getByUid(questionnaireUid).run
    if (questionnaire.next.isEmpty || questionnaire.isComplete) {
      ZIO.fail(DomainError("Invalid questionnaire state")).run
    }

    if (questionnaire.next.get != questionUid) {
      ZIO.fail(DomainError(s"Invalid question uid: $questionUid")).run
    }

    if (answer != 1 && answer != 1) {
      ZIO.fail(DomainError(s"Invalid answer: ${answer}")).run
    }

    // TODO: check if already submitted

    val submission = SubmissionEntity(
      uid = SubmissionUid(UUID.randomUUID()),
      questionnaireUid = questionnaireUid,
      questionUid = questionUid,
      answer = answer
    )

    submissionRepository.add(submission).run

    val updatedQuestionnaire = updateQuestionnare(questionnaire).run

    updatedQuestionnaire
  }

  private def updateQuestionnare(
    questionnaire: QuestionnaireEntity
  ): IO[DomainError, QuestionnaireEntity] = defer {
    val question = questionRepository.getAll().run
    val submissions = submissionRepository.getByQuestionnaireUid(questionnaire.uid).run

    val answeredQuestionUids = submissions.map(s => s.questionUid).toSet
    val unansweredQuestions = question.filter(q =>
      !answeredQuestionUids.contains(q.uid) && !questionnaire.afterNext.contains(q.uid)
    )

    val nextQuestionUid = questionnaire.afterNext
    val afterNextQuestionUid = if (unansweredQuestions.nonEmpty) {
      val index = Random().nextInt(unansweredQuestions.size)
      Some(unansweredQuestions(index).uid)
    } else {
      None
    }

    val updated = questionnaire.copy(
      next = nextQuestionUid,
      afterNext = afterNextQuestionUid,
      isComplete = unansweredQuestions.isEmpty
    )

    questionnaireRepository.update(updated).run
  }
}
