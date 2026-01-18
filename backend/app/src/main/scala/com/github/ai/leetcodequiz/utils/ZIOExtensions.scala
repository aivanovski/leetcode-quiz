package com.github.ai.leetcodequiz.utils

import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.IO
import zio.http.Response

extension [A](io: IO[DomainError, A]) {
  def transformError(): IO[Response, A] =
    io.mapError(_.toDomainResponse())
}
