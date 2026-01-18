package com.github.ai.leetcodequiz.utils

import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.http.Request
import zio.*
import zio.direct.*

extension (request: Request) {
  def getLastUrlParameter(): ZIO[Any, DomainError, String] = {
    val parameter = request.url.toString
      .removeSuffixAfter("?")
      .split("/")
      .filter(_.nonEmpty)
      .lastOption
      .getOrElse("")

    if (parameter.nonEmpty) {
      ZIO.succeed(parameter)
    } else {
      ZIO.fail(DomainError(message = "Invalid id parameter"))
    }
  }
}

def parseIdFromUrl(request: Request): IO[DomainError, Long] = defer {
  val lastParam = request.getLastUrlParameter().run

  val id = ZIO
    .attempt(lastParam.toLong)
    .mapError(DomainError(_))
    .run

  id
}
