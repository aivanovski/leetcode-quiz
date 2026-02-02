package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.{execute, given}
import com.github.ai.leetcodequiz.data.db.model.{DataSyncEntity, SyncType}
import com.github.ai.leetcodequiz.data.db.model.SyncType.given
import com.github.ai.leetcodequiz.data.db.model.SyncUid.given
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import doobie.implicits.*
import doobie.util.transactor.Transactor
import zio.{IO, Task}

import java.time.ZoneOffset

class DataSyncEntityDao(
  private val transactor: Transactor[Task]
) {

  def getLatestSync(syncType: SyncType): IO[DatabaseError, Option[DataSyncEntity]] = {
    sql"""
        SELECT uid, sync_type, timestamp
        FROM data_syncs
        WHERE sync_type = $syncType
        ORDER BY timestamp_value DESC
        LIMIT 1
      """
      .query[DataSyncEntity]
      .option
      .execute(transactor)
  }

  def add(sync: DataSyncEntity): IO[DatabaseError, DataSyncEntity] = {
    val milliseconds = sync.timestamp
      .atZone(ZoneOffset.UTC)
      .toInstant
      .toEpochMilli

    sql"""
        INSERT INTO data_syncs (uid, sync_type, timestamp, timestamp_value)
        VALUES (${sync.uid.toString}, ${sync.syncType}, ${sync.timestamp}, $milliseconds)
      """.update.run
      .map(_ => sync)
      .execute(transactor)
  }
}
