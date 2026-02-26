package com.github.ai.leetcodequiz.data.db.repository

import com.github.ai.leetcodequiz.data.db.dao.QuestionEntityDao
import com.github.ai.leetcodequiz.data.db.model.{
  ChallengeListType,
  ProblemId,
  QuestionEntity,
  QuestionUid
}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import slick.jdbc.SQLiteProfile.api.*

class QuestionRepository(
  private val dao: QuestionEntityDao
) {

  def getAll() = dao.getAll()
  def getByListType(listType: ChallengeListType) = dao.getByListType(listType)
  def getByUid(uid: QuestionUid) = dao.getByUid(uid)
  def add(question: QuestionEntity) = dao.add(question)
  def update(question: QuestionEntity) = dao.update(question)
  def delete(uid: QuestionUid) = dao.deleteByUid(uid)
  def findByProblemId(problemId: ProblemId) = dao.findByProblemId(problemId)
}
