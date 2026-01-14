package com.github.ai.leetcodequiz.utils

import com.github.ai.leetcodequiz.entity.exception.DomainError

import java.io.{PrintWriter, StringWriter}

extension (exception: Throwable)
  def stackTraceToString(): String = {
    val writer = new StringWriter()
    exception.printStackTrace(new PrintWriter(writer))
    writer.toString
  }

extension (exception: Throwable)
  def toDomainError(): DomainError = {
    DomainError(
      message = exception.getMessage.some,
      cause = exception.some
    )
  }
