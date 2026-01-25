package com.github.ai.leetcodequiz.domain.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.ai.leetcodequiz.data.db.model.UserUid
import com.github.ai.leetcodequiz.data.db.repository.UserRepository
import com.github.ai.leetcodequiz.entity.AppEnvironment.{DEBUG, PROD}
import com.github.ai.leetcodequiz.entity.exception.{DomainError, InvalidAuthTokenError}
import com.github.ai.leetcodequiz.entity.{CliArguments, JwtData}
import zio.*
import zio.direct.*

import java.time.Instant
import java.util.{Date, UUID}

class AuthService(
  private val jwtData: JwtData,
  private val appArguments: CliArguments,
  private val userRepository: UserRepository
) {

  def createToken(userUid: UserUid): String = {
    val now = Instant.now()
    val algorithm = Algorithm.HMAC256(jwtData.secret)

    val timeToLiveInMillis = appArguments.environment match {
      case DEBUG => 30.days.toMillis
      case PROD => 2.hours.toMillis
    }

    JWT
      .create()
      .withIssuer(jwtData.issuer)
      .withAudience(jwtData.audience)
      .withSubject(userUid.toString)
      .withIssuedAt(Date.from(now))
      .withExpiresAt(Date.from(now.plusMillis(timeToLiveInMillis)))
      .sign(algorithm)
  }

  def validateToken(token: String): IO[DomainError, UserUid] = defer {
    val algorithm = Algorithm.HMAC256(jwtData.secret)
    val verifier = JWT
      .require(algorithm)
      .withIssuer(jwtData.issuer)
      .withAudience(jwtData.audience)
      .build()

    val decodedToken = ZIO
      .attempt(verifier.verify(token))
      .mapError(error => InvalidAuthTokenError(cause = Some(error)))
      .run

    val subject = Option(decodedToken.getSubject).getOrElse("")
    val userId = ZIO
      .attempt(UserUid(UUID.fromString(subject)))
      .mapError(error => InvalidAuthTokenError(cause = Some(error)))
      .run

    userRepository
      .getByUid(userId)
      .mapError(error => InvalidAuthTokenError(cause = Some(error)))
      .run

    userId
  }
}
