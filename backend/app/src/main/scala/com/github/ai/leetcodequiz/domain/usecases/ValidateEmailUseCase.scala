package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*

class ValidateEmailUseCase {

  private val emailRegex =
    "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$".r

  def isValidEmail(email: String): IO[DomainError, Boolean] =
    if (emailRegex.matches(email)) {
      ZIO.succeed(true)
    } else {
      ZIO.fail(DomainError(s"Invalid email: $email"))
    }
}
