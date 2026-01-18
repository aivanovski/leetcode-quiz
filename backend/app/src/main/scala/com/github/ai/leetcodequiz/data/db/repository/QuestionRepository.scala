package com.github.ai.leetcodequiz.data.db.repository

import com.github.ai.leetcodequiz.data.db.dao.QuestionEntityDao
import com.github.ai.leetcodequiz.data.db.model.{QuestionEntity, QuestionUid}

class QuestionRepository(
  private val dao: QuestionEntityDao
) {

  def getAll() = dao.getAll()
  def add(question: QuestionEntity) = dao.add(question)
  def update(question: QuestionEntity) = dao.update(question)
  def delete(uid: QuestionUid) = dao.delete(uid)
}
