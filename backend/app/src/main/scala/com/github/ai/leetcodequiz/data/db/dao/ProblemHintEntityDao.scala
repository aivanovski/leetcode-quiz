package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.{AppDatabase, SlickMappers}
import com.github.ai.leetcodequiz.data.db.model.{ProblemHintEntity, ProblemHintId, ProblemId}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import slick.jdbc.SQLiteProfile.api.*
import zio.*

class ProblemHintEntityDao(
  db: AppDatabase
) extends Dao(db = db.context, table = db.ProblemHintsTable) {

  import SlickMappers.given

  def getAll(): IO[DatabaseError, List[ProblemHintEntity]] =
    queryAll()

  def getByProblemId(problemId: ProblemId): IO[DatabaseError, List[ProblemHintEntity]] =
    query(_.problemId === problemId)

  def add(hint: ProblemHintEntity): IO[DatabaseError, ProblemHintEntity] = {
    db.run(
      table
        .returning(table.map(_.id))
        .into((entity, id) => entity.copy(id = id)) += hint
    )
  }

  def addBatch(hints: List[ProblemHintEntity]): IO[DatabaseError, List[ProblemHintEntity]] = {
    db.run(
      table
        .returning(table.map(_.id))
        .++=(hints)
    ).map { ids =>
      hints.zip(ids).map { case (hint, id) => hint.copy(id = id) }
    }
  }

  def deleteByProblemId(problemId: ProblemId): IO[DatabaseError, Unit] =
    delete(_.problemId === problemId)
}
