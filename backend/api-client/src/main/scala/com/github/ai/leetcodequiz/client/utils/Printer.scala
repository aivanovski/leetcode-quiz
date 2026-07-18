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

    Console.printLine(s"Response[code=${response.status.code}]:").run
    Console.printLine(TextFormat.printToString(responseDto)).run

    ()
  }
}
