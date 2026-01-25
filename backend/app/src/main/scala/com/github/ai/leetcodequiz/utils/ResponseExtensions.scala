package com.github.ai.leetcodequiz.utils

import com.github.ai.leetcodequiz.api.ErrorMessageDto
import com.github.ai.leetcodequiz.entity.exception.{AuthError, DomainError}
import com.github.ai.leetcodequiz.utils.*

import java.util.Base64
import zio.http.{Body, Header, Headers, Response, Status}
import zio.json.*

import java.nio.charset.StandardCharsets.UTF_8
import scala.annotation.tailrec

extension (exception: DomainError) {
  def toDomainResponse(): Response = {
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

    val response = ErrorMessageDto(
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
        Headers(Header.WWWAuthenticate.Bearer(realm = "Access"))
      } else {
        Headers.empty
      },
      body = Body.fromString(response.toJsonPretty, UTF_8)
    )
  }

  @tailrec
  private def getRootCauseOrSelf(error: Throwable): Throwable = {
    if (error.getCause == null) error
    else getRootCauseOrSelf(error.getCause)
  }
}
