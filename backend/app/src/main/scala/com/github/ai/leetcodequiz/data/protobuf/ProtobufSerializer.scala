package com.github.ai.leetcodequiz.data.protobuf

import com.github.ai.leetcodequiz.entity.exception.DomainError
import scalapb.GeneratedMessage
import scalapb.GeneratedMessageCompanion
import zio.*
import zio.http.Body

class ProtobufSerializer {

  def serializeToBody[T <: GeneratedMessage](data: T): Body =
    Body.fromArray(data.toByteArray)

  def deserialize[T <: GeneratedMessage](data: Array[Byte])(using
    companion: GeneratedMessageCompanion[T]
  ): IO[DomainError, T] =
    ZIO.attempt(companion.parseFrom(data)).mapError(DomainError(_))
}
