package com.github.ai.leetcodequiz.domain

import com.github.ai.leetcodequiz.domain.usecases.SetupTestDataUseCase
import com.github.ai.leetcodequiz.entity.AppEnvironment.DEBUG
import com.github.ai.leetcodequiz.entity.CliArguments
import zio.{IO, ZIO}
import zio.direct.{defer, run}

class StartupService {

  def startupServer() = {
    defer {
      val appArguments = ZIO.service[CliArguments].run

      val jobService = ZIO.service[ScheduledJobService].run
      jobService.startScheduledJobs().run

      if (appArguments.environment == DEBUG) {
        val setupTestDataUseCase = ZIO.service[SetupTestDataUseCase].run
        setupTestDataUseCase.setupDefaultData().run
      }

      ZIO.unit.run
    }
  }
}
