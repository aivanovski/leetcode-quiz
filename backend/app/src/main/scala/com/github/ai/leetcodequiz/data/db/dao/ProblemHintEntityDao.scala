package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.execute
import com.github.ai.leetcodequiz.data.db.model.{ProblemHintEntity, ProblemHintId, ProblemId}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import doobie.Update
import doobie.implicits.*
import doobie.syntax.ConnectionIOOps
import doobie.util.transactor.Transactor
import zio.{IO, Task, ZIO}
import fs2.Stream

class ProblemHintEntityDao(
  private val transactor: Transactor[Task]
) {

  def getAll(): IO[DatabaseError, List[ProblemHintEntity]] = {
    sql"""
          SELECT id, problem_id, hint
          FROM problem_hints
       """
      .query[ProblemHintEntity]
      .to[List]
      .execute(transactor)
  }

  def getByProblemId(problemId: ProblemId): IO[DatabaseError, List[ProblemHintEntity]] = {
    sql"""
        SELECT id, problem_id, hint
        FROM problem_hints
        WHERE problem_id = $problemId
      """
      .query[ProblemHintEntity]
      .to[List]
      .execute(transactor)
  }

  def add(hint: ProblemHintEntity): IO[DatabaseError, ProblemHintEntity] = {
    sql"""
        INSERT INTO problem_hints (problem_id, hint)
        VALUES (${hint.problemId}, ${hint.hint})
      """.update
      .withUniqueGeneratedKeys[Long]("id")
      .map(generatedId => hint.copy(id = ProblemHintId(generatedId)))
      .execute(transactor)
  }

//  def addBatch(hints: List[ProblemHintEntity]): IO[DatabaseError, List[ProblemHintEntity]] = {
//    ZIO
//      .foreach(hints)(add)
//      .mapError(e => e)
//  }

  def addBatch(hints: List[ProblemHintEntity]): IO[DatabaseError, List[ProblemHintEntity]] = {
    val sql = "INSERT INTO problem_hints (problem_id, hint) VALUES (?, ?)"
    Update[(ProblemId, String)](sql)
      .updateManyWithGeneratedKeys[Long]("id")(hints.map(h => (h.problemId, h.hint)))
      .compile
      .toList
      .map(ids => hints.zip(ids).map { case (hint, id) => hint.copy(id = ProblemHintId(id)) })
      .execute(transactor)
  }

  def deleteByProblemId(problemId: ProblemId): IO[DatabaseError, Unit] = {
    sql"""
        DELETE FROM problem_hints
        WHERE problem_id = $problemId
      """.update.run
      .execute(transactor)
      .unit
  }

}
