package com.github.ai.leetcodequiz.data.doobie.dao

import com.github.ai.leetcodequiz.data.doobie.execute
import com.github.ai.leetcodequiz.data.doobie.model.{QuestionEntity, QuestionUid, ProblemId}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import doobie.implicits.*
import doobie.syntax.ConnectionIOOps
import doobie.util.transactor.Transactor
import zio.{IO, Task}

class QuestionEntityDao(
  private val transactor: Transactor[Task]
) {

  def getAll(): IO[DatabaseError, List[QuestionEntity]] = {
    sql"""
        SELECT uid, problem_id, question, complexity
        FROM questions
      """
      .query[QuestionEntity]
      .to[List]
      .execute(transactor)
  }

  def add(question: QuestionEntity): IO[DatabaseError, QuestionEntity] = {
    sql"""
        INSERT INTO questions (uid, problem_id, question, complexity)
        VALUES (${question.uid}, ${question.problemId}, ${question.question}, ${question.complexity})
      """.update.run
      .map(_ => question)
      .execute(transactor)
  }

  def update(question: QuestionEntity): IO[DatabaseError, QuestionEntity] = {
    sql"""
        UPDATE questions
        SET problem_id = ${question.problemId}, question = ${question.question}, complexity = ${question.complexity}
        WHERE uid = ${question.uid}
      """.update.run
      .map(_ => question)
      .execute(transactor)
  }

  def delete(uid: QuestionUid): IO[DatabaseError, Unit] = {
    sql"""
        DELETE FROM questions
        WHERE uid = $uid
      """.update.run
      .execute(transactor)
      .unit
  }
}
