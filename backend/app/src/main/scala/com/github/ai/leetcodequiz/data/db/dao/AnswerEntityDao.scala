package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.execute
import com.github.ai.leetcodequiz.data.db.model.{
  QuestionUid,
  QuestionnaireUid,
  AnswerEntity,
  AnswerUid
}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import doobie.implicits.*
import doobie.util.transactor.Transactor
import zio.*

class AnswerEntityDao(
  private val transactor: Transactor[Task]
) {

  def getAll(): IO[DatabaseError, List[AnswerEntity]] = {
    sql"""
          SELECT uid, questionnaire_uid, question_uid, answer
          FROM answers
        """
      .query[AnswerEntity]
      .to[List]
      .execute(transactor)
  }

  def getByQuestionnaireUid(
    questionnaireUid: QuestionnaireUid
  ): IO[DatabaseError, List[AnswerEntity]] = {
    sql"""
        SELECT uid, questionnaire_uid, question_uid, answer
        FROM answers
        WHERE questionnaire_uid = $questionnaireUid
      """
      .query[AnswerEntity]
      .to[List]
      .execute(transactor)
  }

  def add(entities: List[AnswerEntity]): IO[DatabaseError, List[AnswerEntity]] =
    ZIO.collectAll(entities.map(entity => add(entity)))

  def add(answer: AnswerEntity): IO[DatabaseError, AnswerEntity] = {
    sql"""
        INSERT INTO answers (uid, questionnaire_uid, question_uid, answer)
        VALUES (${answer.uid}, ${answer.questionnaireUid}, ${answer.questionUid}, ${answer.answer})
      """.update.run
      .map(_ => answer)
      .execute(transactor)
  }

  def deleteByQuestionnaireUid(questionnaireUid: QuestionnaireUid): IO[DatabaseError, Unit] = {
    sql"""
        DELETE FROM answers
        WHERE questionnaire_uid = $questionnaireUid
        """.update.run
      .execute(transactor)
      .unit
  }

  def update(entity: AnswerEntity): IO[DatabaseError, AnswerEntity] = {
    sql"""
          UPDATE answers
          SET questionnaire_uid = ${entity.questionnaireUid}, question_uid = ${entity.questionUid}, answer = ${entity.answer}
          WHERE uid = ${entity.uid}
        """.update.run
      .map(_ => entity)
      .execute(transactor)
  }
}
