package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.db.model.{UserEntity, UserUid}
import com.github.ai.leetcodequiz.data.db.repository.UserRepository
import com.github.ai.leetcodequiz.domain.PasswordService
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

import java.util.UUID

class SetupTestDataUseCase(
  private val passwordService: PasswordService,
  private val userRepository: UserRepository
) {

  def setupDefaultData(): IO[DomainError, Unit] = defer {
    setupTestUser().run

    ()
  }

  private def setupTestUser(): IO[DomainError, Unit] = defer {
    val existingUser = userRepository.findByEmail(TestData.TEST_USER_EMAIL).run
    if (existingUser.isEmpty) {
      userRepository
        .add(
          UserEntity(
            uid = UserUid(new UUID(0, 0)),
            name = TestData.TEST_USER_NAME,
            email = TestData.TEST_USER_EMAIL,
            passwordHash = passwordService.hashPassword(TestData.TEST_USER_PASSWORD)
          )
        )
        .run
    }
  }

  private object TestData {
    val TEST_USER_NAME = "admin"
    val TEST_USER_EMAIL = "admin@mail.com"
    val TEST_USER_PASSWORD = "abc123"
  }
}
