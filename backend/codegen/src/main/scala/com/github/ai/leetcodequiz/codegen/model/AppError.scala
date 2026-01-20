package com.github.ai.leetcodequiz.codegen.model

sealed class AppError(message: String)

case class InvalidArgumentsError(message: String) extends AppError(message)

case class IOError(exception: Throwable) extends AppError(exception.getMessage)

case class ScalaSyntaxError(message: String) extends AppError(message)
