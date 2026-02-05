package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.{AppDatabase, SlickMappers}
import com.github.ai.leetcodequiz.data.db.model.{QuestionEntity, QuestionUid}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import slick.jdbc.SQLiteProfile.api.*
import zio.*

class QuestionEntityDao(
  db: AppDatabase
) extends Dao(db = db.context, table = db.QuestionsTable) {

  import SlickMappers.given

  def getAll(): IO[DatabaseError, List[QuestionEntity]] =
    queryAll()

  def add(question: QuestionEntity): IO[DatabaseError, QuestionEntity] =
    insert(question)

  def update(question: QuestionEntity): IO[DatabaseError, QuestionEntity] =
    updateOne(_.uid === question.uid, question)

  def deleteQuestion(uid: QuestionUid): IO[DatabaseError, Unit] =
    deleteOne(_.uid === uid)
}
