package com.github.ai.leetcodequiz.entity.exception

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class DomainError(
  val message: Option[String],
  val cause: Option[Throwable]
) extends Exception(
      message.orNull,
      cause.orNull
    )

object DomainError {
  def apply(message: String): DomainError =
    new DomainError(Some(message), None)

  def apply(cause: Throwable): DomainError =
    new DomainError(None, Some(cause))
}

class ParsingError(
  message: Option[String],
  cause: Option[Throwable]
) extends DomainError(message, cause)

object ParsingError {
  def apply(message: String): ParsingError =
    new ParsingError(Some(message), None)

  def apply(cause: Throwable): ParsingError =
    new ParsingError(None, Some(cause))
}

class JsonDeserializationError(
  typeOf: Class[?],
  cause: Option[Throwable] = None
) extends ParsingError(
      message = Some(s"Unable to deserialize type: ${typeOf.getTypeName}"),
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

class FailedToFindEntityError(
  entityType: Class[?],
  criteria: String
) extends DatabaseError(
      message = Some(s"Failed to find ${entityType.getSimpleName} by: "),
      cause = None
    )
