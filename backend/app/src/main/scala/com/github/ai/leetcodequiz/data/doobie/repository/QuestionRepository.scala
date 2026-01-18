package com.github.ai.leetcodequiz.data.doobie.repository

import com.github.ai.leetcodequiz.data.doobie.dao.QuestionEntityDao
import com.github.ai.leetcodequiz.data.doobie.model.{QuestionEntity, QuestionUid}

class QuestionRepository(
  private val dao: QuestionEntityDao
) {

  def getAll() = dao.getAll()
  def add(question: QuestionEntity) = dao.add(question)
  def update(question: QuestionEntity) = dao.update(question)
  def delete(uid: QuestionUid) = dao.delete(uid)
}
