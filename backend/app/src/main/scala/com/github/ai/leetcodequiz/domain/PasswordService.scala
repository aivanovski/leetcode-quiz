package com.github.ai.leetcodequiz.domain

import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.utils.some
import org.mindrot.jbcrypt.BCrypt
import zio.*

class PasswordService {

  def hashPassword(password: String): String =
    BCrypt.hashpw(password, BCrypt.gensalt())

  def isPasswordMatch(password: String, hashedPassword: String): Boolean = {
    BCrypt.checkpw(password, hashedPassword)
  }
}
