package com.github.ai.leetcodequiz.domain

import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.{IO, ZIO}
import zio.direct.{defer, run}

class StartupService {

  def startupServer() = {
    defer {
      //      if (cliArguments.isPopulateTestData) {
      //        fillTestDataUseCase.createTestData().run
      //      }

      val jobService = ZIO.service[ScheduledJobService].run
      jobService.startScheduledJobs().run

      ZIO.unit.run
    }
  }
}
