package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.{AppDatabase, SlickMappers}
import com.github.ai.leetcodequiz.data.db.model.{
  ChallengeListType,
  ProblemId,
  QuestionEntity,
  QuestionUid
}
import com.github.ai.leetcodequiz.entity.exception.{DatabaseError, FailedToFindEntityError}
import slick.jdbc.SQLiteProfile.api.*
import zio.*

class QuestionEntityDao(
  db: AppDatabase
) extends Dao(db = db.context, table = db.QuestionsTable) {

  import SlickMappers.given

  def getAll(): IO[DatabaseError, List[QuestionEntity]] =
    queryAll()

  def getByUid(uid: QuestionUid): IO[DatabaseError, QuestionEntity] =
    queryOne(table => table.uid === uid)
      .flatMap(o => ZIO.fromOption(o))
      .mapError(_ => FailedToFindEntityError(classOf[QuestionEntity], criteria = s"uid == $uid"))

  def getByListType(listType: ChallengeListType): IO[DatabaseError, List[QuestionEntity]] =
    query(_.listType === listType)

  def add(question: QuestionEntity): IO[DatabaseError, QuestionEntity] =
    insert(question)

  def update(question: QuestionEntity): IO[DatabaseError, QuestionEntity] =
    updateOne(_.uid === question.uid, question)

  def deleteByUid(uid: QuestionUid): IO[DatabaseError, Unit] =
    deleteOne(_.uid === uid)

  def findByProblemId(problemId: ProblemId): IO[DatabaseError, Option[QuestionEntity]] =
    queryOne(_.problemId === problemId)
}
