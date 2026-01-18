package com.github.ai.leetcodequiz.data.doobie.repository

import com.github.ai.leetcodequiz.data.doobie.dao.DataSyncEntityDao
import com.github.ai.leetcodequiz.data.doobie.model.{DataSyncEntity, SyncType}

class DataSyncRepository(
  private val dao: DataSyncEntityDao
) {

  def getLatestSync(syncType: SyncType) = dao.getLatestSync(syncType)
  def add(sync: DataSyncEntity) = dao.add(sync)

}
