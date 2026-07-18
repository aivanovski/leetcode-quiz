package com.github.ai.leetcodequiz.utils

import com.github.ai.leetcodequiz.api.{ErrorMessageDto, ResponseDto, ResponseType}
import com.github.ai.leetcodequiz.data.protobuf.ProtobufSerializer
import com.github.ai.leetcodequiz.entity.exception.{AuthError, DomainError}
import com.github.ai.leetcodequiz.utils.*

import java.util.Base64
import zio.http.{Header, Headers, MediaType, Response, Status}

import java.nio.charset.StandardCharsets.UTF_8
import scala.annotation.tailrec

extension (exception: DomainError) {
  def toDomainResponse(serializer: ProtobufSerializer): Response = {
    val hasMessage = exception.message.isDefined
    val hasCause = exception.cause.isDefined

    // TODO: do not print stacktrace when app in PROD

    val rootCause = exception.cause
      .map(cause => getRootCauseOrSelf(cause))
      .getOrElse(exception)

    val stacktrace = rootCause.stackTraceToString()
    val encodedStacktrace = Base64.getEncoder.encodeToString(stacktrace.getBytes(UTF_8))
    val stacktraceLines = stacktrace
      .split("\n")
      .map(_.replaceAll("\t", "  "))
      .toList

    val isAuthError = rootCause.isInstanceOf[AuthError] || exception.isInstanceOf[AuthError]

    val errorMessage = ErrorMessageDto(
      message = exception.message.map(_.trim).getOrElse(""),
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

  @tailrec
  private def getRootCauseOrSelf(error: Throwable): Throwable = {
    if (error.getCause == null) error
    else getRootCauseOrSelf(error.getCause)
  }
}
