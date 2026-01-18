package com.github.ai.leetcodequiz.data.db.model

import java.time.LocalDateTime
import java.util.UUID

case class DataSyncEntity(
  uid: SyncUid,
  syncType: SyncType,
  timestamp: LocalDateTime
)
