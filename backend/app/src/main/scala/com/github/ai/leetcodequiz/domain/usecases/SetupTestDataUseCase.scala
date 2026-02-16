package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.db.model.{UserEntity, UserUid}
import com.github.ai.leetcodequiz.data.db.repository.UserRepository
import com.github.ai.leetcodequiz.domain.PasswordService
import com.github.ai.leetcodequiz.entity.ApplicationConfig
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

import java.util.UUID

class SetupTestDataUseCase(
  private val passwordService: PasswordService,
  private val userRepository: UserRepository,
  private val appConfig: ApplicationConfig
) {

  def setupDefaultData(): IO[DomainError, Unit] = defer {
    setupDebugUsers().run

    ()
  }

  private def setupDebugUsers(): IO[DomainError, Unit] = defer {
    val debugUserCredentials = readDebugUserCredentials().run

    debugUserCredentials.foreach { case (email, password) =>
      val existingUser = userRepository.findByEmail(email).run
      if (existingUser.isEmpty) {
        userRepository
          .add(
            UserEntity(
              uid = UserUid(UUID.randomUUID()),
              name = email,
              email = email,
              passwordHash = passwordService.hashPassword(password)
            )
          )
          .run
      }
    }
  }

  private def readDebugUserCredentials(): IO[DomainError, List[(String, String)]] =
    ZIO.succeed(appConfig.debugUsers.map(user => (user.email, user.password)))
}
