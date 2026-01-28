package com.github.ai.leetcodequiz.data.db.repository

import com.github.ai.leetcodequiz.data.db.dao.{
  AnswerEntityDao,
  NextQuestionEntityDao,
  QuestionnaireEntityDao
}
import com.github.ai.leetcodequiz.data.db.model.{
  AnswerEntity,
  NextQuestionEntity,
  NextQuestionUid,
  QuestionnaireEntity,
  QuestionnaireUid,
  QuestionUid,
  AnswerUid
}
import com.github.ai.leetcodequiz.entity.{Questionnaire, Answer}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

import java.util.UUID

class QuestionnaireRepository(
  private val questionnaireDao: QuestionnaireEntityDao,
  private val questionDao: NextQuestionEntityDao,
  private val answerDao: AnswerEntityDao
) {

  def getAll(): IO[DomainError, List[Questionnaire]] = defer {
    val questionnaires = questionnaireDao.getAll().run
    val allQuestions = questionDao.getAll().run
    val allAnswers = answerDao.getAll().run

    questionnaires.map { questionnaire =>
      val questions = allQuestions.filter(_.questionnaireUid == questionnaire.uid)
      val answers = allAnswers.filter(_.questionnaireUid == questionnaire.uid)

      toDomainEntity(questionnaire, questions, answers)
    }
  }

  def getByUid(uid: QuestionnaireUid): IO[DomainError, Questionnaire] = defer {
    val questionnaire = questionnaireDao.getByUid(uid).run
    val questions = questionDao.getByQuestionnaireUid(questionnaireUid = uid).run
    val answers = answerDao.getByQuestionnaireUid(questionnaireUid = uid).run

    toDomainEntity(questionnaire, questions, answers)
  }

  def add(questionnaire: Questionnaire): IO[DomainError, Questionnaire] = defer {
    val (entity, questions, answers) = toDatabaseEntities(questionnaire)

    questionnaireDao.add(entity).run
    questionDao.add(questions).run
    answerDao.add(answers).run

    questionnaire
  }

  def update(questionnaire: Questionnaire): IO[DomainError, Questionnaire] = defer {
    val (entity, questions, answers) = toDatabaseEntities(questionnaire)

    questionnaireDao.update(entity).run
    questionDao.deleteByQuestionnaireUid(questionnaire.uid).run
    questionDao.add(questions).run
    answerDao.deleteByQuestionnaireUid(questionnaire.uid).run
    answerDao.add(answers).run

    questionnaire
  }

  def addOrUpdateAnswer(
    questionnaireUid: QuestionnaireUid,
    questionUid: QuestionUid,
    answer: Int
  ): IO[DomainError, Unit] = defer {
    val questionnaire = answerDao.getByQuestionnaireUid(questionnaireUid).run

    val existing = questionnaire.find(answer => answer.questionUid == questionUid)

    if (existing.isDefined) {
      // update
      answerDao
        .update(
          existing.get.copy(
            answer = answer
          )
        )
        .run
    } else {
      // add new
      answerDao
        .add(
          AnswerEntity(
            uid = AnswerUid(UUID.randomUUID()),
            questionnaireUid = questionnaireUid,
            questionUid = questionUid,
            answer = answer
          )
        )
        .run
    }

    ()
  }

  private def toDomainEntity(
    entity: QuestionnaireEntity,
    questions: List[NextQuestionEntity],
    answers: List[AnswerEntity]
  ) = Questionnaire(
    uid = entity.uid,
    questions = questions.map(_.questionUid),
    answers = answers.map(a => Answer(uid = a.questionUid, answer = a.answer)),
    isComplete = entity.isComplete
  )

  private def toDatabaseEntities(
    questionnaire: Questionnaire
  ): (QuestionnaireEntity, List[NextQuestionEntity], List[AnswerEntity]) = {
    val entity = QuestionnaireEntity(
      uid = questionnaire.uid,
      isComplete = questionnaire.isComplete
    )

    val questions = questionnaire.questions.map { questionUid =>
      NextQuestionEntity(
        uid = NextQuestionUid(UUID.randomUUID()),
        questionnaireUid = questionnaire.uid,
        questionUid = questionUid
      )
    }

    val answers = questionnaire.answers.map { answer =>
      AnswerEntity(
        uid = AnswerUid(UUID.randomUUID()),
        questionnaireUid = questionnaire.uid,
        questionUid = answer.uid,
        answer = answer.answer
      )
    }

    (entity, questions, answers)
  }
}
