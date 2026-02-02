package com.github.ai.leetcodequiz.domain.jobs

import com.github.ai.leetcodequiz.data.db.model.{DataSyncEntity, SyncType, SyncUid}
import com.github.ai.leetcodequiz.data.db.repository.DataSyncRepository
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID

trait ScheduledJob(
  private val syncRepository: DataSyncRepository,
  val interval: Duration,
  val syncType: SyncType,
  val dependsOn: List[SyncType] = List.empty
) {

  def run(): IO[DomainError, Unit]

  def shouldRunSync(): IO[DomainError, Boolean] = defer {
    val lastSync = syncRepository.getLatestSync(syncType).run
    val timeThreshold = LocalDateTime.now(ZoneOffset.UTC).minus(interval)

    lastSync.isEmpty || lastSync.get.timestamp.isBefore(timeThreshold)
  }

  def onSyncStart(): IO[DomainError, SyncUid] = defer {
    ZIO.logInfo(s"Synchronization started: $syncType").run

    SyncUid(UUID.randomUUID())
  }

  def syncComplete(uid: SyncUid): IO[DomainError, Unit] = defer {
    ZIO.logInfo(s"Synchronization complete: $syncType").run

    syncRepository
      .add(
        DataSyncEntity(
          uid = uid,
          syncType = syncType,
          timestamp = LocalDateTime.now(ZoneOffset.UTC)
        )
      )
      .run

    ()
  }
}
