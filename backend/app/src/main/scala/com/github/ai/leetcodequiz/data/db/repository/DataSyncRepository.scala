package com.github.ai.leetcodequiz.data.db.repository

import com.github.ai.leetcodequiz.data.db.dao.DataSyncEntityDao
import com.github.ai.leetcodequiz.data.db.model.{DataSyncEntity, SyncType}

class DataSyncRepository(
  private val dao: DataSyncEntityDao
) {

  def getLatestSync(syncType: SyncType) = dao.getLatestSync(syncType)
  def add(sync: DataSyncEntity) = dao.add(sync)

}
