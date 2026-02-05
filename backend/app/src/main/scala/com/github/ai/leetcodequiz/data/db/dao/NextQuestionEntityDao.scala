package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.{AppDatabase, SlickMappers}
import com.github.ai.leetcodequiz.data.db.model.{NextQuestionEntity, NextQuestionUid, QuestionnaireUid}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import slick.jdbc.SQLiteProfile.api.*
import zio.*

class NextQuestionEntityDao(
  db: AppDatabase
) extends Dao(db = db.context, table = db.NextQuestionsTable) {

  import SlickMappers.given

  def getByUid(uid: NextQuestionUid): IO[DatabaseError, Option[NextQuestionEntity]] =
    queryOne(_.uid === uid)

  def getByQuestionnaireUid(
    questionnaireUid: QuestionnaireUid
  ): IO[DatabaseError, List[NextQuestionEntity]] =
    query(_.questionnaireUid === questionnaireUid)

  def getAll(): IO[DatabaseError, List[NextQuestionEntity]] =
    queryAll()

  def add(entities: List[NextQuestionEntity]): IO[DatabaseError, List[NextQuestionEntity]] =
    insertAll(entities)

  def add(entity: NextQuestionEntity): IO[DatabaseError, NextQuestionEntity] =
    insert(entity)

  def update(entity: NextQuestionEntity): IO[DatabaseError, NextQuestionEntity] =
    updateOne(_.uid === entity.uid, entity)

  def deleteNextQuestion(uid: NextQuestionUid): IO[DatabaseError, Unit] =
    deleteOne(_.uid === uid)

  def deleteByQuestionnaireUid(questionnaireUid: QuestionnaireUid): IO[DatabaseError, Unit] =
    delete(_.questionnaireUid === questionnaireUid)
}
