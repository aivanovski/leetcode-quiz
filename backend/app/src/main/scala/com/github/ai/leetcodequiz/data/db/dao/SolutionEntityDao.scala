package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.execute
import com.github.ai.leetcodequiz.data.db.model.{ProblemId, SolutionEntity, SolutionUid}
import com.github.ai.leetcodequiz.entity.exception.{DatabaseError, FailedToFindEntityError}
import doobie.implicits.*
import doobie.util.transactor.Transactor
import zio.*

class SolutionEntityDao(
  private val transactor: Transactor[Task]
) {

  def getByUid(uid: SolutionUid): IO[DatabaseError, SolutionEntity] = {
    findByUid(uid)
      .flatMap { opt =>
        ZIO
          .fromOption(opt)
          .mapError(_ =>
            FailedToFindEntityError(entityType = classOf[SolutionEntity], criteria = s"uid = $uid")
          )
      }
  }

  def findByUid(uid: SolutionUid): IO[DatabaseError, Option[SolutionEntity]] = {
    sql"""
          SELECT uid, problem_id, path, content
          FROM solutions
          WHERE uid = $uid
        """
      .query[SolutionEntity]
      .option
      .execute(transactor)
  }

  def findByProblemId(problemId: ProblemId): IO[DatabaseError, List[SolutionEntity]] = {
    sql"""
        SELECT uid, problem_id, path, content
        FROM solutions
        WHERE problem_id = $problemId
      """
      .query[SolutionEntity]
      .to[List]
      .execute(transactor)
  }

  def getAll(): IO[DatabaseError, List[SolutionEntity]] = {
    sql"""
        SELECT uid, problem_id, path, content
        FROM solutions
      """
      .query[SolutionEntity]
      .to[List]
      .execute(transactor)
  }

  def add(solution: SolutionEntity): IO[DatabaseError, SolutionEntity] = {
    sql"""
        INSERT INTO solutions (uid, problem_id, path, content)
        VALUES (${solution.uid}, ${solution.problemId}, ${solution.path}, ${solution.content})
      """.update.run
      .map(_ => solution)
      .execute(transactor)
  }

  def update(solution: SolutionEntity): IO[DatabaseError, SolutionEntity] = {
    sql"""
        UPDATE solutions
        SET problem_id = ${solution.problemId}, path = ${solution.path}, content = ${solution.content}
        WHERE uid = ${solution.uid}
      """.update.run
      .map(_ => solution)
      .execute(transactor)
  }

  def delete(uid: SolutionUid): IO[DatabaseError, Unit] = {
    sql"""
        DELETE FROM solutions
        WHERE uid = $uid
      """.update.run
      .execute(transactor)
      .unit
  }

  def deleteByProblemId(problemId: ProblemId): IO[DatabaseError, Unit] = {
    sql"""
        DELETE FROM solutions
        WHERE problem_id = $problemId
      """.update.run
      .execute(transactor)
      .unit
  }
}
