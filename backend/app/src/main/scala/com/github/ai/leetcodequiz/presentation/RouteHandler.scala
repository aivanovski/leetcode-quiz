package com.github.ai.leetcodequiz.presentation

import com.github.ai.leetcodequiz.api.ProtoResponseMapper
import com.github.ai.leetcodequiz.data.protobuf.ProtobufSerializer
import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.utils.{toDomainResponse}
import scalapb.GeneratedMessage
import zio.direct.*
import zio.http.*
import zio.{IO, Tag, ZIO}

def protoHandler[Controller: Tag](
  mapper: (Controller, Request) => IO[DomainError, GeneratedMessage]
): Handler[Controller & ProtobufSerializer, Response, Request, Response] = {
  handler { (request: Request) =>
    defer {
      val controller = ZIO.service[Controller].run
      val serializer = ZIO.service[ProtobufSerializer].run

      mapper
        .apply(controller, request)
        .map { message =>
          Response(
            headers = Headers(Header.ContentType(MediaType.application.`octet-stream`)),
            body = serializer.serializeToBody(ProtoResponseMapper.createResponseDto(message))
          )
        }
        .mapError(error => error.toDomainResponse(serializer))
        .run
    }
  }
}
