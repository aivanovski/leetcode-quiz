package com.github.ai.leetcodequiz.presentation.controllers

import com.github.ai.leetcodequiz.api.{
  LoginRequest,
  LoginResponse,
  RefreshTokenRequest,
  RefreshTokenResponse,
  SignupRequest,
  SignupResponse
}
import com.github.ai.leetcodequiz.data.db.model.{UserEntity, UserUid}
import com.github.ai.leetcodequiz.data.db.repository.UserRepository
import com.github.ai.leetcodequiz.data.protobuf.ProtobufSerializer
import com.github.ai.leetcodequiz.domain.PasswordService
import com.github.ai.leetcodequiz.domain.authentication.{AuthHandler, AuthService}
import com.github.ai.leetcodequiz.entity.{
  JwtTokenType,
  JwtTokens,
  MessageWithHeaders,
  RefreshToken
}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.utils.{readBodyAsBytes, toUserDto}
import zio.*
import zio.direct.*
import zio.http.Cookie.SameSite.Lax
import zio.http.{Cookie, Header, Path, Request}

import java.util.UUID

class AuthController(
  private val userRepository: UserRepository,
  private val passwordService: PasswordService,
  private val authService: AuthService,
  private val protobufSerializer: ProtobufSerializer
) {

  def signup(request: Request): IO[DomainError, SignupResponse] = defer {
    val body = request
      .readBodyAsBytes()
      .flatMap { bytes => protobufSerializer.deserialize[SignupRequest](bytes) }
      .run

    val existingUser = userRepository.findByEmail(body.email).run
    if (existingUser.isDefined) {
      ZIO.fail(DomainError("User already exists")).run
    }

    val user = UserEntity(
      uid = UserUid(UUID.randomUUID()),
      name = body.name,
      email = body.email,
      passwordHash = passwordService.hashPassword(body.password)
    )

    userRepository.add(user).run

    val tokens = authService.createTokens(user.uid)

    SignupResponse(
      token = tokens.token.toString,
      refreshToken = tokens.refreshToken.toString,
      user = toUserDto(user)
    )
  }

  def login(request: Request): IO[DomainError, MessageWithHeaders[LoginResponse]] = defer {
    val body = request
      .readBodyAsBytes()
      .flatMap { bytes => protobufSerializer.deserialize[LoginRequest](bytes) }
      .run

    val userOption = userRepository
      .findByEmail(body.email)
      .mapError(DomainError(_))
      .run

    if (userOption.isEmpty) {
      ZIO.fail(DomainError("Invalid email or password")).run
    }

    val user = userOption.get
    val isValidPassword =
      passwordService.isPasswordMatch(body.password, user.passwordHash)

    if (!isValidPassword) {
      ZIO.fail(DomainError("Invalid email or password")).run
    }

    val tokens = authService.createTokens(user.uid)

    val response = LoginResponse(
      token = tokens.token.toString,
      refreshToken = tokens.refreshToken.toString,
      user = toUserDto(user)
    )

    MessageWithHeaders(
      headers = createAuthHeaders(tokens),
      message = response
    )
  }

  def refresh(request: Request): IO[DomainError, MessageWithHeaders[RefreshTokenResponse]] = defer {
    val body = request
      .readBodyAsBytes()
      .flatMap { bytes => protobufSerializer.deserialize[RefreshTokenRequest](bytes) }
      .run

    val token = RefreshToken(body.refreshToken)

    val userUid = authService
      .validateRefreshToken(token)
      .run

    val tokens = authService.createTokens(userUid = userUid)

    val userOption = userRepository
      .getByUid(userUid)
      .run

    val response = RefreshTokenResponse(
      token = tokens.token.toString,
      refreshToken = tokens.refreshToken.toString
    )

    MessageWithHeaders(
      headers = createAuthHeaders(tokens),
      message = response
    )
  }

  private def createAuthHeaders(tokens: JwtTokens): List[Header] = {
    val authTokenCookie = Cookie.Response(
      name = AuthHandler.AuthTokenCookieName,
      content = tokens.token.toString,
      isHttpOnly = true,
      isSecure = true,
      sameSite = Option(Lax),
      path = Option(Path("/api")),
      maxAge = Some(authService.getTokenTimeToLive(JwtTokenType.AUTH_TOKEN))
    )

    val refreshTokenHeader = Cookie.Response(
      name = AuthHandler.RefreshTokenCookieName,
      content = tokens.refreshToken.toString,
      isHttpOnly = true,
      isSecure = true,
      path = Option(Path("/api/auth/refresh")),
      maxAge = Some(authService.getTokenTimeToLive(JwtTokenType.REFRESH_TOKEN))
    )

    List(
      Header.SetCookie(authTokenCookie),
      Header.SetCookie(refreshTokenHeader)
    )
  }
}
