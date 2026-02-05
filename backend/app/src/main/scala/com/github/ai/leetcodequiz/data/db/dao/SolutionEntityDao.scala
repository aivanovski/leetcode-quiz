package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.{AppDatabase, SlickMappers}
import com.github.ai.leetcodequiz.data.db.model.{ProblemId, SolutionEntity, SolutionUid}
import com.github.ai.leetcodequiz.entity.exception.{DatabaseError, FailedToFindEntityError}
import slick.jdbc.SQLiteProfile.api.*
import zio.*
import zio.direct.*

class SolutionEntityDao(
  db: AppDatabase
) extends Dao(db = db.context, table = db.SolutionsTable) {

  import SlickMappers.given

  def getByUid(uid: SolutionUid): IO[DatabaseError, SolutionEntity] = defer {
    findByUid(uid).run match
      case Some(value) => value
      case None =>
        ZIO
          .fail(FailedToFindEntityError(entityType = classOf[SolutionEntity], criteria = s"uid = $uid"))
          .run
  }

  def findByUid(uid: SolutionUid): IO[DatabaseError, Option[SolutionEntity]] =
    queryOne(_.uid === uid)

  def findByProblemId(problemId: ProblemId): IO[DatabaseError, List[SolutionEntity]] =
    query(_.problemId === problemId)

  def getAll(): IO[DatabaseError, List[SolutionEntity]] =
    queryAll()

  def add(solution: SolutionEntity): IO[DatabaseError, SolutionEntity] =
    insert(solution)

  def update(solution: SolutionEntity): IO[DatabaseError, SolutionEntity] =
    updateOne(_.uid === solution.uid, solution)

  def deleteSolution(uid: SolutionUid): IO[DatabaseError, Unit] =
    deleteOne(_.uid === uid)

  def deleteByProblemId(problemId: ProblemId): IO[DatabaseError, Unit] =
    delete(_.problemId === problemId)
}
