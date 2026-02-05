package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.{AppDatabase, SlickMappers}
import com.github.ai.leetcodequiz.data.db.model.{DataSyncEntity, SyncType}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import slick.jdbc.SQLiteProfile.api.*
import zio.{IO, ZIO}

class DataSyncEntityDao(
  db: AppDatabase
) extends Dao(db = db.context, table = db.DataSyncsTable) {

  import SlickMappers.given

  private val insertProjection = table.map(t => (t.uid, t.syncType, t.timestamp, t.timestampValue))

  def getLatestSync(syncType: SyncType): IO[DatabaseError, Option[DataSyncEntity]] = {
    ZIO
      .fromFuture { _ =>
        db.context.run(
          table
            .filter(_.syncType === syncType)
            .sortBy(_.timestampValue.desc)
            .result
            .headOption
        )
      }
      .mapError(DatabaseError(_))
  }

  def add(sync: DataSyncEntity): IO[DatabaseError, DataSyncEntity] = {
    ZIO
      .fromFuture { _ =>
        db.context.run(
          insertProjection += ((sync.uid, sync.syncType, sync.timestamp, sync.timestampValue))
        )
      }
      .map(_ => sync)
      .mapError(DatabaseError(_))
  }
}
