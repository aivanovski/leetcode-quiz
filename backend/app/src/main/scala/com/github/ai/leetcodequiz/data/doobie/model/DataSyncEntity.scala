package com.github.ai.leetcodequiz.data.doobie.model

import java.time.LocalDateTime
import java.util.UUID

case class DataSyncEntity(
  uid: SyncUid,
  syncType: SyncType,
  timestamp: LocalDateTime
)
