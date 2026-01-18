package com.github.ai.leetcodequiz.utils

import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.{IO, ZIO}

import java.util.UUID

extension (str: String) {
  def parseUid(): IO[DomainError, UUID] = {
    ZIO
      .attempt(UUID.fromString(str))
      .mapError(error => new DomainError(message = "Invalid UUID".some, cause = error.some))
  }
}
