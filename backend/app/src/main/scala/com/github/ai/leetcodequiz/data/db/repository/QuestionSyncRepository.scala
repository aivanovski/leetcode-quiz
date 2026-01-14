package com.github.ai.leetcodequiz.data.db.repository

import com.github.ai.leetcodequiz.data.db.dao.QuestionSyncEntityDao
import com.github.ai.leetcodequiz.data.db.model.QuestionSyncEntity

class QuestionSyncRepository(
  private val dao: QuestionSyncEntityDao
) {

  def getLastSync() = dao.getLastSync()
  def add(sync: QuestionSyncEntity) = dao.add(sync)

}
