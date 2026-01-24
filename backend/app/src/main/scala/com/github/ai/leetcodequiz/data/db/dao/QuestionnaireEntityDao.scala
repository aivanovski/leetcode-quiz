package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.execute
import com.github.ai.leetcodequiz.data.db.given
import com.github.ai.leetcodequiz.data.db.model.{QuestionUid, QuestionnaireEntity, QuestionnaireUid}
import com.github.ai.leetcodequiz.data.db.model.QuestionnaireUid.given
import com.github.ai.leetcodequiz.data.db.model.QuestionUid.given
import com.github.ai.leetcodequiz.entity.exception.{DatabaseError, FailedToFindEntityError}
import doobie.implicits.*
import doobie.syntax.ConnectionIOOps
import doobie.util.transactor.Transactor
import zio.{IO, Task, ZIO}

class QuestionnaireEntityDao(
  private val transactor: Transactor[Task]
) {

  def getAll(): IO[DatabaseError, List[QuestionnaireEntity]] = {
    sql"""
        SELECT uid, is_complete
        FROM questionnaires
      """
      .query[QuestionnaireEntity]
      .to[List]
      .execute(transactor)
  }

  def findByUid(uid: QuestionnaireUid): IO[DatabaseError, Option[QuestionnaireEntity]] = {
    sql"""
        SELECT uid, is_complete
        FROM questionnaires
        WHERE uid = $uid
      """
      .query[QuestionnaireEntity]
      .option
      .execute(transactor)
  }

  def getByUid(uid: QuestionnaireUid): IO[DatabaseError, QuestionnaireEntity] = {
    findByUid(uid)
      .flatMap { opt =>
        ZIO
          .fromOption(opt)
          .mapError(_ => FailedToFindEntityError(classOf[QuestionnaireEntity], "uid = $uid"))
      }
  }

  def add(questionnaire: QuestionnaireEntity): IO[DatabaseError, QuestionnaireEntity] = {
    sql"""
        INSERT INTO questionnaires (uid, is_complete)
        VALUES (${questionnaire.uid}, ${questionnaire.isComplete})
      """.update.run
      .map(_ => questionnaire)
      .execute(transactor)
  }

  def addBatch(
    questionnaires: List[QuestionnaireEntity]
  ): IO[DatabaseError, List[QuestionnaireEntity]] = {
    ZIO
      .foreach(questionnaires)(add)
      .mapError(e => e)
  }

  def update(questionnaire: QuestionnaireEntity): IO[DatabaseError, QuestionnaireEntity] = {
    sql"""
        UPDATE questionnaires
        SET is_complete = ${questionnaire.isComplete}
        WHERE uid = ${questionnaire.uid}
      """.update.run
      .map(_ => questionnaire)
      .execute(transactor)
  }

  def delete(uid: QuestionnaireUid): IO[DatabaseError, Unit] = {
    sql"""
        DELETE FROM questionnaires
        WHERE uid = $uid
      """.update.run
      .execute(transactor)
      .unit
  }
}
