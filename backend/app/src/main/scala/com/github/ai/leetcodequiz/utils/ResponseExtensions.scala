package com.github.ai.leetcodequiz.utils

import com.github.ai.leetcodequiz.api.ErrorMessageDto
import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.utils.*
import com.google.gson.GsonBuilder

import java.util.{Base64, Collections, Optional}
import zio.http.{Body, Response, Status}

import java.nio.charset.StandardCharsets.UTF_8
import scala.annotation.tailrec

extension (exception: DomainError) {
  def toDomainResponse(): Response = {
    val hasMessage = exception.message.isDefined
    val hasCause = exception.cause.isDefined

    val exceptionToPrint = exception.cause
      .map(cause => getRootCauseOrSelf(cause))
      .getOrElse(exception)

    val stacktrace = exceptionToPrint.stackTraceToString()
    val encodedStacktrace = Base64.getEncoder.encodeToString(stacktrace.getBytes(UTF_8))
    val stacktraceLines = stacktrace
      .split("\n")
      .map(_.replaceAll("\t", "  "))
      .toList

    val response = ErrorMessageDto(
      message = exception.message.map(_.trim).getOrElse(""),
      exception = exceptionToPrint.toString.trim,
      stacktraceBase64 = encodedStacktrace,
      stacktraceLines = stacktraceLines.toJavaList()
    )

    // TODO: use JsonSerializable
    val gson = GsonBuilder().setPrettyPrinting().create()

    Response.error(
      status = Status.BadRequest,
      body = Body.fromString(gson.toJson(response), UTF_8)
    )
  }

  @tailrec
  private def getRootCauseOrSelf(error: Throwable): Throwable = {
    if (error.getCause == null) error
    else getRootCauseOrSelf(error.getCause)
  }
}
