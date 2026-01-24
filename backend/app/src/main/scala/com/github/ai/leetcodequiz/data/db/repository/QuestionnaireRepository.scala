package com.github.ai.leetcodequiz.data.db.repository

import com.github.ai.leetcodequiz.data.db.dao.{NextQuestionEntityDao, QuestionnaireEntityDao}
import com.github.ai.leetcodequiz.data.db.model.{
  NextQuestionEntity,
  NextQuestionUid,
  QuestionnaireEntity,
  QuestionnaireUid
}
import com.github.ai.leetcodequiz.entity.Questionnaire
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

import java.util.UUID

class QuestionnaireRepository(
  private val questionnaireDao: QuestionnaireEntityDao,
  private val nextQuestionDao: NextQuestionEntityDao
) {

  def getAll(): IO[DomainError, List[Questionnaire]] = defer {
    val questionnaires = questionnaireDao.getAll().run
    val nextQuestions = nextQuestionDao.getAll().run

    questionnaires.map { questionnaire =>
      val questions = nextQuestions.filter(_.questionnaireUid == questionnaire.uid)

      toDomainEntity(questionnaire, questions)
    }
  }

  def getByUid(uid: QuestionnaireUid): IO[DomainError, Questionnaire] = defer {
    val questionnaire = questionnaireDao.getByUid(uid).run
    val nextQuestions = nextQuestionDao.getByQuestionnaireUid(questionnaireUid = uid).run

    toDomainEntity(questionnaire, nextQuestions)
  }

  def add(questionnaire: Questionnaire): IO[DomainError, Questionnaire] = defer {
    val (entity, questions) = toDatabaseEntities(questionnaire)

    questionnaireDao.add(entity).run
    nextQuestionDao.add(questions).run

    questionnaire
  }

  def update(questionnaire: Questionnaire): IO[DomainError, Questionnaire] = defer {
    val (entity, questions) = toDatabaseEntities(questionnaire)

    questionnaireDao.update(entity).run
    nextQuestionDao.deleteByQuestionnaireUid(questionnaire.uid).run
    nextQuestionDao.add(questions).run

    questionnaire
  }

  private def toDomainEntity(
    entity: QuestionnaireEntity,
    nextQuestions: List[NextQuestionEntity]
  ) = Questionnaire(
    uid = entity.uid,
    nextQuestions = nextQuestions.map(_.questionUid),
    isComplete = entity.isComplete
  )

  private def toDatabaseEntities(
    questionnaire: Questionnaire
  ): (QuestionnaireEntity, List[NextQuestionEntity]) = {
    val entity = QuestionnaireEntity(
      uid = questionnaire.uid,
      isComplete = questionnaire.isComplete
    )

    val nextQuestions = questionnaire.nextQuestions.map { questionUid =>
      NextQuestionEntity(
        uid = NextQuestionUid(UUID.randomUUID()),
        questionnaireUid = questionnaire.uid,
        questionUid = questionUid
      )
    }

    (entity, nextQuestions)
  }
}
