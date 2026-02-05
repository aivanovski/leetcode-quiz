package com.github.ai.leetcodequiz.domain

import com.github.ai.leetcodequiz.data.db.model.SyncType
import com.github.ai.leetcodequiz.data.db.repository.DataSyncRepository
import com.github.ai.leetcodequiz.domain.jobs.{
  JobScheduler,
  ScheduledJob,
  SyncProblemsJob,
  SyncQuestionsJob,
  SyncSolutionsJob
}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.{defer, run}

class ScheduledJobService(
  private val syncRepository: DataSyncRepository
) {

  private val scheduler = JobScheduler[SyncType]()

  def startScheduledJobs() = defer {
    val syncProblemsJob = ZIO.service[SyncProblemsJob].run
    val syncQuestionsJob = ZIO.service[SyncQuestionsJob].run
    val syncSolutionsJob = ZIO.service[SyncSolutionsJob].run

    val allJobs = List(syncProblemsJob, syncQuestionsJob, syncSolutionsJob)

    runJob(allJobs)
      .repeat(Schedule.fixed(1.hour))
      .catchAllCause { cause =>
        ZIO.succeed(println(cause.prettyPrint))
      }
      .forkDaemon
      .run

    ZIO.logInfo("Scheduled job service started successfully.").run
  }

  private def runJob(
    jobs: List[ScheduledJob]
  ): IO[DomainError, Unit] = defer {
    val syncTypeToDependenciesMap = jobs.map(job => (job.syncType, job.dependsOn)).toMap
    val runPlan = scheduler.createSchedule(syncTypeToDependenciesMap).run
    val syncTypeToJobMap = jobs.map(job => (job.syncType, job)).toMap

    for (syncType <- runPlan) {
      val job = syncTypeToJobMap(syncType)
      if (job.shouldRunSync().run) {
        job.run().run
      }
    }

    ()
  }
}
