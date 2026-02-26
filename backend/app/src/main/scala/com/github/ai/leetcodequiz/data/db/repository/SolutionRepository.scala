package com.github.ai.leetcodequiz.data.db.repository

import com.github.ai.leetcodequiz.data.db.dao.SolutionEntityDao
import com.github.ai.leetcodequiz.data.db.model.{ProblemId, SolutionEntity, SolutionUid, SourceType}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import zio.IO

class SolutionRepository(
  private val dao: SolutionEntityDao
) {

  def getByUid(uid: SolutionUid): IO[DatabaseError, SolutionEntity] =
    dao.getByUid(uid)

  def findByUid(uid: SolutionUid): IO[DatabaseError, Option[SolutionEntity]] =
    dao.findByUid(uid)

  def findByProblemId(problemId: ProblemId): IO[DatabaseError, List[SolutionEntity]] =
    dao.findByProblemId(problemId)

  def getAll(): IO[DatabaseError, List[SolutionEntity]] =
    dao.getAll()

  def findBySourceType(sourceType: SourceType): IO[DatabaseError, List[SolutionEntity]] =
    dao.findBySourceType(sourceType)

  def add(solution: SolutionEntity): IO[DatabaseError, SolutionEntity] =
    dao.add(solution)

  def update(solution: SolutionEntity): IO[DatabaseError, SolutionEntity] =
    dao.update(solution)

  def delete(uid: SolutionUid): IO[DatabaseError, Unit] =
    dao.deleteSolution(uid)

  def deleteByProblemId(problemId: ProblemId): IO[DatabaseError, Unit] =
    dao.deleteByProblemId(problemId)
}
