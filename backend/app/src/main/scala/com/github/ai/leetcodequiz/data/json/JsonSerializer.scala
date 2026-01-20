package com.github.ai.leetcodequiz.data.json

import com.github.ai.leetcodequiz.entity.exception.ParsingError
import zio.{IO, ZIO}
import zio.json.*

class JsonSerializer {

  def serialize[T](data: T)(implicit encoder: JsonEncoder[T]): String =
    data.toJsonPretty

  def deserialize[T](data: String)(implicit decoder: JsonDecoder[T]): IO[ParsingError, T] =
    ZIO
      .fromEither(data.fromJson[T](using decoder))
      .mapError(ParsingError(_))
}
