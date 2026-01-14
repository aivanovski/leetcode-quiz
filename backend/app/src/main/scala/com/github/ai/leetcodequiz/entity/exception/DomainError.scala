package com.github.ai.leetcodequiz.entity.exception

class DomainError(
  val message: Option[String] = None,
  val cause: Option[Throwable] = None
) extends Exception(
      message.orNull,
      cause.orNull
    )

class ParsingError(
  message: String,
  cause: Option[Throwable] = None
) extends DomainError(message = Some(message), cause = cause)

class JsonDeserializationError(
  typeOf: Class[?],
  cause: Option[Throwable] = None
) extends ParsingError(
      message = s"Unable to deserialize type: ${typeOf.getTypeName}",
      cause = cause
    )

class FileSystemError(
  message: Option[String],
  cause: Option[Throwable]
) extends DomainError(message, cause)

object FileSystemError {
  def apply(message: String): FileSystemError =
    new FileSystemError(Some(message), None)

  def apply(cause: Throwable): FileSystemError =
    new FileSystemError(None, Some(cause))
}

class DatabaseError(
  message: Option[String],
  cause: Option[Throwable]
) extends DomainError(message, cause)

object DatabaseError {
  def apply(message: String): DatabaseError =
    new DatabaseError(Some(message), None)

  def apply(cause: Throwable): DatabaseError =
    new DatabaseError(None, Some(cause))
}
