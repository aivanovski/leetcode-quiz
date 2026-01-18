package com.github.ai.leetcodequiz.domain

import com.github.ai.leetcodequiz.domain.jobs.{SyncProblemsJob, SyncQuestionsJob}
import zio.*
import zio.direct.{defer, run}

class ScheduledJobService {

  def startScheduledJobs() = defer {
    // Schedule the job to run at specific interval
    val syncProblemsJob = ZIO.service[SyncProblemsJob].run
    val syncQuestionsJob = ZIO.service[SyncQuestionsJob].run

    syncProblemsJob
      .run()
      .repeat(Schedule.fixed(syncProblemsJob.interval))
      .forkDaemon
      .run

    syncQuestionsJob
      .run()
      .repeat(Schedule.fixed(syncQuestionsJob.interval))
      .forkDaemon
      .run

    ZIO.logInfo("Scheduled job service started successfully.").run
  }
}
