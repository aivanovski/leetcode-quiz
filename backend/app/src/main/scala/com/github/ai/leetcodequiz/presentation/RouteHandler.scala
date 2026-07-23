package com.github.ai.leetcodequiz.presentation

import com.github.ai.leetcodequiz.api.ProtoResponseMapper
import com.github.ai.leetcodequiz.data.protobuf.ProtobufSerializer
import com.github.ai.leetcodequiz.entity.MessageWithHeaders
import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.utils.toDomainResponse
import scalapb.GeneratedMessage
import zio.direct.*
import zio.http.*
import zio.{Tag, ZIO}

def protoHandler[Controller: Tag](
  mapper: (Controller, Request) => ZIO[Any, DomainError, GeneratedMessage | MessageWithHeaders[?]]
): Handler[Controller & ProtobufSerializer, Response, Request, Response] = {
  handler { (request: Request) =>
    defer {
      val controller = ZIO.service[Controller].run
      val serializer = ZIO.service[ProtobufSerializer].run

      mapper
        .apply(controller, request)
        .map { message =>
          val headers = Headers(Header.ContentType(MediaType.application.`octet-stream`))

          val (response, additionalHeaders): (GeneratedMessage, Headers) =
            message match {
              case value: MessageWithHeaders[?] =>
                (value.message, Headers(value.headers))

              case value: GeneratedMessage =>
                (value, Headers.empty)
            }

          Response(
            headers = headers ++ additionalHeaders,
            body = serializer.serializeToBody(ProtoResponseMapper.createResponseDto(response))
          )
        }
        .mapError(error => error.toDomainResponse(serializer))
        .run
    }
  }
}
