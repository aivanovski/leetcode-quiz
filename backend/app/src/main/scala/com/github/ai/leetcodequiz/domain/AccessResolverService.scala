package com.github.ai.leetcodequiz.domain

import com.github.ai.leetcodequiz.data.db.model.UserUid
import com.github.ai.leetcodequiz.entity.{Access, AccessResolutionResult}
import com.github.ai.leetcodequiz.entity.Access.{DENIED, GRANTED}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.utils.some
import zio.*
import zio.direct.*

class AccessResolverService(
  private val passwordService: PasswordService
) {

  private def isPasswordMatch(
    password: String,
    passwordHash: Option[String]
  ): IO[DomainError, Unit] = {
    if (password.isEmpty && passwordHash.isEmpty) {
      ZIO.unit
    } else {
      val isMatch = passwordService.isPasswordMatch(
        password = password,
        hashedPassword = passwordHash.getOrElse("")
      )

      if (isMatch) {
        ZIO.unit
      } else {
        ZIO.fail(DomainError(message = "Password doesn't match".some))
      }
    }
  }
}
