package com.github.ai.leetcodequiz.data.doobie.dao

import com.github.ai.leetcodequiz.data.doobie.execute
import com.github.ai.leetcodequiz.data.doobie.model.{ProblemEntity, ProblemId}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import doobie.implicits.*
import doobie.syntax.ConnectionIOOps
import doobie.util.transactor.Transactor
import zio.{IO, Task}

class ProblemEntityDao(
  private val transactor: Transactor[Task]
) {

  def getById(id: ProblemId): IO[DatabaseError, Option[ProblemEntity]] = {
    sql"""
        SELECT id, title, content, category, url, difficulty, likes, dislikes
        FROM problems
        WHERE id = $id
      """
      .query[ProblemEntity]
      .option
      .execute(transactor)
  }

  def getAll(): IO[DatabaseError, List[ProblemEntity]] = {
    sql"""
        SELECT id, title, content, category, url, difficulty, likes, dislikes
        FROM problems
      """
      .query[ProblemEntity]
      .to[List]
      .execute(transactor)
  }

  def add(problem: ProblemEntity): IO[DatabaseError, ProblemEntity] = {
    sql"""
        INSERT INTO problems (id, title, content, category, url, difficulty, likes, dislikes)
        VALUES (${problem.id}, ${problem.title}, ${problem.content}, ${problem.category}, ${problem.url}, ${problem.difficulty}, ${problem.likes}, ${problem.dislikes})
      """.update.run
      .map(_ => problem)
      .execute(transactor)
  }

  def update(problem: ProblemEntity): IO[DatabaseError, ProblemEntity] = {
    sql"""
        UPDATE problems
        SET title = ${problem.title}, content = ${problem.content}, category = ${problem.category}, 
            url = ${problem.url}, difficulty = ${problem.difficulty}, likes = ${problem.likes}, dislikes = ${problem.dislikes}
        WHERE id = ${problem.id}
      """.update.run
      .map(_ => problem)
      .execute(transactor)
  }

  def delete(id: ProblemId): IO[DatabaseError, Unit] = {
    sql"""
        DELETE FROM problems
        WHERE id = $id
      """.update.run
      .execute(transactor)
      .unit
  }

}
