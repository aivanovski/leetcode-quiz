package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.{AppDatabase, SlickMappers}
import com.github.ai.leetcodequiz.data.db.model.{QuestionnaireEntity, QuestionnaireUid}
import com.github.ai.leetcodequiz.entity.exception.{DatabaseError, FailedToFindEntityError}
import slick.jdbc.SQLiteProfile.api.*
import zio.*
import zio.direct.*

class QuestionnaireEntityDao(
  db: AppDatabase
) extends Dao(db = db.context, table = db.QuestionnairesTable) {

  import SlickMappers.given

  def getAll(): IO[DatabaseError, List[QuestionnaireEntity]] =
    queryAll()

  def findByUid(uid: QuestionnaireUid): IO[DatabaseError, Option[QuestionnaireEntity]] =
    queryOne(_.uid === uid)

  def getByUid(uid: QuestionnaireUid): IO[DatabaseError, QuestionnaireEntity] = defer {
    findByUid(uid).run match
      case Some(value) => value
      case None =>
        ZIO
          .fail(FailedToFindEntityError(classOf[QuestionnaireEntity], s"uid = $uid"))
          .run
  }

  def add(questionnaire: QuestionnaireEntity): IO[DatabaseError, QuestionnaireEntity] =
    insert(questionnaire)

  def addBatch(
    questionnaires: List[QuestionnaireEntity]
  ): IO[DatabaseError, List[QuestionnaireEntity]] =
    insertAll(questionnaires)

  def update(questionnaire: QuestionnaireEntity): IO[DatabaseError, QuestionnaireEntity] =
    updateOne(_.uid === questionnaire.uid, questionnaire)

  def deleteQuestionnaire(uid: QuestionnaireUid): IO[DatabaseError, Unit] =
    deleteOne(_.uid === uid)
}
