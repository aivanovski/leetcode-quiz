package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.{AppDatabase, SlickMappers}
import com.github.ai.leetcodequiz.data.db.model.{AnswerEntity, QuestionnaireUid}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import slick.jdbc.SQLiteProfile.api.*
import zio.*

class AnswerEntityDao(
  db: AppDatabase
) extends Dao(db = db.context, table = db.AnswersTable) {

  import SlickMappers.given

  def getAll(): IO[DatabaseError, List[AnswerEntity]] =
    queryAll()

  def queryByQuestionnaireUid(
    questionnaireUid: QuestionnaireUid
  ): IO[DatabaseError, List[AnswerEntity]] =
    query(table => table.questionnaireUid === questionnaireUid)

  def deleteByQuestionnaireUid(
    questionnaireUid: QuestionnaireUid
  ): IO[DatabaseError, Unit] =
    delete(table => table.questionnaireUid === questionnaireUid)

  def update(entity: AnswerEntity): IO[DatabaseError, AnswerEntity] =
    updateOne(table => table.uid === entity.uid, entity = entity)
}
