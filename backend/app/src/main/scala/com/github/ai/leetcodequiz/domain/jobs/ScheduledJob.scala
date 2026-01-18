package com.github.ai.leetcodequiz.domain.jobs

import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.{Duration, IO}

trait ScheduledJob {
  val interval: Duration
  def run(): IO[DomainError, Unit]
}
