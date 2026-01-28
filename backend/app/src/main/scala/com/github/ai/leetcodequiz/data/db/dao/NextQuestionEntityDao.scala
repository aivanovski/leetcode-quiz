package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.execute
import com.github.ai.leetcodequiz.data.db.model.{
  NextQuestionEntity,
  NextQuestionUid,
  QuestionnaireUid
}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import doobie.implicits.*
import doobie.syntax.ConnectionIOOps
import doobie.util.transactor.Transactor
import zio.{IO, Task, ZIO}

class NextQuestionEntityDao(
  private val transactor: Transactor[Task]
) {

  def getByUid(uid: NextQuestionUid): IO[DatabaseError, Option[NextQuestionEntity]] = {
    sql"""
        SELECT uid, questionnaire_uid, question_uid
        FROM next_questions
        WHERE uid = $uid
      """
      .query[NextQuestionEntity]
      .option
      .execute(transactor)
  }

  def getByQuestionnaireUid(
    questionnaireUid: QuestionnaireUid
  ): IO[DatabaseError, List[NextQuestionEntity]] = {
    sql"""
        SELECT uid, questionnaire_uid, question_uid
        FROM next_questions
        WHERE questionnaire_uid = $questionnaireUid
      """
      .query[NextQuestionEntity]
      .to[List]
      .execute(transactor)
  }

  def getAll(): IO[DatabaseError, List[NextQuestionEntity]] = {
    sql"""
        SELECT uid, questionnaire_uid, question_uid
        FROM next_questions
      """
      .query[NextQuestionEntity]
      .to[List]
      .execute(transactor)
  }

  def add(entities: List[NextQuestionEntity]): IO[DatabaseError, List[NextQuestionEntity]] =
    ZIO.collectAll(entities.map(entity => add(entity)))

  def add(entity: NextQuestionEntity): IO[DatabaseError, NextQuestionEntity] = {
    sql"""
        INSERT INTO next_questions (uid, questionnaire_uid, question_uid)
        VALUES (${entity.uid}, ${entity.questionnaireUid}, ${entity.questionUid})
      """.update.run
      .map(_ => entity)
      .execute(transactor)
  }

  def update(entity: NextQuestionEntity): IO[DatabaseError, NextQuestionEntity] = {
    sql"""
        UPDATE next_questions
        SET questionnaire_uid = ${entity.questionnaireUid}, question_uid = ${entity.questionUid}
        WHERE uid = ${entity.uid}
      """.update.run
      .map(_ => entity)
      .execute(transactor)
  }

  def delete(uid: NextQuestionUid): IO[DatabaseError, Unit] = {
    sql"""
        DELETE FROM next_questions
        WHERE uid = $uid
      """.update.run
      .execute(transactor)
      .unit
  }

  def deleteByQuestionnaireUid(questionnaireUid: QuestionnaireUid): IO[DatabaseError, Unit] = {
    sql"""
        DELETE FROM next_questions
        WHERE questionnaire_uid = $questionnaireUid
      """.update.run
      .execute(transactor)
      .unit
  }
}
