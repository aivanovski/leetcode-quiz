package com.github.ai.leetcodequiz.domain.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.ai.leetcodequiz.data.db.model.UserUid
import com.github.ai.leetcodequiz.data.db.repository.UserRepository
import com.github.ai.leetcodequiz.entity.AppEnvironment.{DEBUG, PROD}
import com.github.ai.leetcodequiz.entity.exception.{
  DomainError,
  InvalidAuthTokenError,
  InvalidPayloadError
}
import com.github.ai.leetcodequiz.entity.{
  ApplicationConfig,
  AuthToken,
  JwtTokenType,
  JwtTokens,
  RefreshToken
}
import zio.*
import zio.direct.*

import java.time.Instant
import java.util.{Date, UUID}

class AuthService(
  private val appConfig: ApplicationConfig,
  private val userRepository: UserRepository
) {

  private val tokenTimeToLive = appConfig.environment match {
    case DEBUG => 30.days
    case PROD => 2.hours
  }

  private val refreshTokenTimeToLive = 60.days

  def createAuthToken(userUid: UserUid): AuthToken = {
    AuthToken(generateToken(userUid, tokenTimeToLive, JwtTokenType.AUTH_TOKEN))
  }

  def createTokens(userUid: UserUid): JwtTokens = {
    JwtTokens(
      token = AuthToken(generateToken(userUid, tokenTimeToLive, JwtTokenType.AUTH_TOKEN)),
      refreshToken = RefreshToken(
        generateToken(userUid, refreshTokenTimeToLive, JwtTokenType.REFRESH_TOKEN)
      )
    )
  }

  def validateAuthToken(token: AuthToken): IO[DomainError, UserUid] = {
    validateToken(
      token = token.toString,
      requestedTokenType = JwtTokenType.AUTH_TOKEN
    )
  }

  def validateRefreshToken(token: RefreshToken): IO[DomainError, UserUid] = {
    validateToken(
      token = token.toString,
      requestedTokenType = JwtTokenType.REFRESH_TOKEN
    )
  }

  private def validateToken(
    token: String,
    requestedTokenType: JwtTokenType
  ): IO[DomainError, UserUid] = defer {
    val algorithm = Algorithm.HMAC256(appConfig.jwt.secret)
    val verifier = JWT
      .require(algorithm)
      .withIssuer(appConfig.jwt.issuer)
      .withAudience(appConfig.jwt.audience)
      .build()

    val decodedToken = ZIO
      .attempt(verifier.verify(token))
      .mapError(error => InvalidAuthTokenError(cause = Some(error)))
      .run

    val subject = Option(decodedToken.getSubject).getOrElse("")
    val tokenTypeClaim = Option(decodedToken.getClaim(Claims.TokenType).asString()).getOrElse("")

    val tokenType = ZIO
      .fromOption(JwtTokenType.fromString(tokenTypeClaim))
      .mapError(_ => InvalidPayloadError())
      .run

    if (tokenType != requestedTokenType) {
      ZIO
        .fail(InvalidPayloadError())
        .run
    }

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

  private def generateToken(
    userUid: UserUid,
    timeToLive: Duration,
    tokenType: JwtTokenType
  ): String = {
    val now = Instant.now()
    val algorithm = Algorithm.HMAC256(appConfig.jwt.secret)

    JWT
      .create()
      .withIssuer(appConfig.jwt.issuer)
      .withAudience(appConfig.jwt.audience)
      .withSubject(userUid.toString)
      .withClaim(Claims.TokenType, tokenType.toString)
      .withIssuedAt(Date.from(now))
      .withExpiresAt(Date.from(now.plusMillis(timeToLive.toMillis)))
      .sign(algorithm)
  }

  private object Claims {
    val TokenType = "tokenType"
  }
}
