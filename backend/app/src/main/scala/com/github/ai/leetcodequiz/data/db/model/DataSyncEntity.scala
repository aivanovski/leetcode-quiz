package com.github.ai.leetcodequiz.data.db.model

import java.time.LocalDateTime
import java.util.UUID

case class DataSyncEntity(
  uid: SyncUid,
  syncType: SyncType,
  timestamp: LocalDateTime,
  timestampValue: Long
)

object DataSyncEntity {
  def apply(
    uid: SyncUid,
    syncType: SyncType,
    timestamp: LocalDateTime
  ): DataSyncEntity = {
    val millis = timestamp.atZone(java.time.ZoneOffset.UTC).toInstant.toEpochMilli
    new DataSyncEntity(uid, syncType, timestamp, millis)
  }
}
