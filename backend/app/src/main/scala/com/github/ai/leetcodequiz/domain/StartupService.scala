package com.github.ai.leetcodequiz.domain

import com.github.ai.leetcodequiz.data.db.AppDatabase
import com.github.ai.leetcodequiz.domain.usecases.SetupTestDataUseCase
import com.github.ai.leetcodequiz.entity.AppEnvironment.DEBUG
import com.github.ai.leetcodequiz.entity.ApplicationConfig
import zio.{IO, ZIO}
import zio.direct.{defer, run}

class StartupService {

  def startupServer() = {
    defer {
      val appConfig = ZIO.service[ApplicationConfig].run
      val db = ZIO.service[AppDatabase].run

      db.initialize().run

      val jobService = ZIO.service[ScheduledJobService].run
      jobService.startScheduledJobs().run

      if (appConfig.environment == DEBUG) {
        val setupTestDataUseCase = ZIO.service[SetupTestDataUseCase].run
        setupTestDataUseCase.setupDefaultData().run
      }

      ZIO.unit.run
    }
  }
}
