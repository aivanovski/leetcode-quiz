package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.{AppDatabase, SlickMappers}
import com.github.ai.leetcodequiz.data.db.model.{ProblemEntity, ProblemId}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import slick.jdbc.SQLiteProfile.api.*
import zio.*

class ProblemEntityDao(
  db: AppDatabase
) extends Dao(db = db.context, table = db.ProblemsTable) {

  import SlickMappers.given

  def getById(id: ProblemId): IO[DatabaseError, Option[ProblemEntity]] =
    queryOne(_.id === id)

  def getAll(): IO[DatabaseError, List[ProblemEntity]] =
    queryAll()

  def add(problem: ProblemEntity): IO[DatabaseError, ProblemEntity] =
    insert(problem)

  def update(problem: ProblemEntity): IO[DatabaseError, ProblemEntity] =
    updateOne(_.id === problem.id, problem)

  def deleteProblem(id: ProblemId): IO[DatabaseError, Unit] =
    deleteOne(_.id === id)
}
