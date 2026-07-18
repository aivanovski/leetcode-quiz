package com.github.ai.leetcodequiz.presentation

import com.github.ai.leetcodequiz.api.{
  ErrorMessageDto,
  GetProblemResponse,
  LoginResponse,
  ProtoResponseMapper,
  ResponseDto,
  ResponseType
}
import com.github.ai.leetcodequiz.data.protobuf.ProtobufSerializer
import com.github.ai.leetcodequiz.entity.exception.{AuthError, DomainError}
import com.github.ai.leetcodequiz.utils.{getRootCauseOrSelf, stackTraceToString, toDomainResponse}
import scalapb.GeneratedMessage
import zio.direct.*
import zio.http.*
import zio.{IO, Tag, ZIO}

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

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
        .mapError(error => createErrorResponse(error, serializer))
        .run
    }
  }
}

private def createErrorResponse(
  error: DomainError,
  serializer: ProtobufSerializer
): Response = {
  val hasMessage = error.message.isDefined
  val hasCause = error.cause.isDefined

  // TODO: do not print stacktrace when app in PROD

  val rootCause = error.cause
    .map(cause => getRootCauseOrSelf(cause))
    .getOrElse(error)

  val stacktrace = rootCause.stackTraceToString()
  val encodedStacktrace = Base64.getEncoder.encodeToString(stacktrace.getBytes(UTF_8))
  val stacktraceLines = stacktrace
    .split("\n")
    .map(_.replaceAll("\t", "  "))
    .toList

  val isAuthError = rootCause.isInstanceOf[AuthError] || error.isInstanceOf[AuthError]

  val errorMessage = ErrorMessageDto(
    message = error.message.map(_.trim).getOrElse(""),
    exception = rootCause.toString.trim,
    stacktraceBase64 = encodedStacktrace,
    stacktraceLines = stacktraceLines
  )

  Response(
    status = if (isAuthError) {
      Status.Unauthorized
    } else {
      Status.BadRequest
    },
    headers = if (isAuthError) {
      Headers(
        List(
          Header.WWWAuthenticate.Bearer(realm = "Access"),
          Header.ContentType(MediaType.application.`octet-stream`)
        )
      )
    } else {
      Headers(
        List(
          Header.ContentType(MediaType.application.`octet-stream`)
        )
      )
    },
    body = serializer.serializeToBody(
      ResponseDto(
        responseType = ResponseType.UNDEFINED,
        errorMessageDto = Some(errorMessage),
        body = ResponseDto.Body.Empty
      )
    )
  )
}
