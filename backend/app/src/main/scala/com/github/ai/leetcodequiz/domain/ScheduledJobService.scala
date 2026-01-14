package com.github.ai.leetcodequiz.domain

import com.github.ai.leetcodequiz.data.file.FileSystemProvider
import com.github.ai.leetcodequiz.domain.usecases.{CloneGithubRepositoryUseCase, SyncQuestionsUseCase}
import com.github.ai.leetcodequiz.entity.RelativePath
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.{defer, run}

trait ScheduledJobService {
  def startScheduledJobs(): IO[DomainError, Unit]
}

class ScheduledJobServiceImpl(
  private val syncQuestionsUseCase: SyncQuestionsUseCase
) extends ScheduledJobService {

  private def syncQuestionsJob(): IO[DomainError, Unit] = defer {
    ZIO.logInfo("Start sync questions job.").run
    syncQuestionsUseCase.syncQuestions().run
    ZIO.logInfo("Sync questions job finished.").run

    ()
  }

  override def startScheduledJobs(): IO[DomainError, Unit] = defer {
    // Schedule the job to run at specific interval
    syncQuestionsJob()
      .repeat(Schedule.fixed(1.hour))
      .forkDaemon
      .run

    ZIO.logInfo("Scheduled job service started successfully.").run
  }
}
