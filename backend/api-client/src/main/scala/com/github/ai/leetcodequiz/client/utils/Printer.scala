package com.github.ai.leetcodequiz.client.utils

import com.github.ai.leetcodequiz.api.ResponseDto
import scalapb.TextFormat
import zio.*
import zio.direct.*
import zio.http.*

class Printer {

  def print(response: Response): IO[Throwable, Unit] = defer {
    val bytes = response.body.asArray.run
    val responseDto = ZIO.attempt(ResponseDto.parseFrom(bytes)).run

    Console.printLine(s"HTTP Status Code: ${response.status.code}").run

    Console.printLine(s"Headers: ${response.headers.size}").run

    response.headers.foreach { header =>
      Console.printLine(s"    ${header.headerName}=${header.renderedValue}").run
    }

    Console.printLine(TextFormat.printToString(responseDto)).run

    ()
  }
}
