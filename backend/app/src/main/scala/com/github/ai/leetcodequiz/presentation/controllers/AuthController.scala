package com.github.ai.leetcodequiz.presentation.controllers

import com.github.ai.leetcodequiz.api.{LoginRequest, SignupRequest}
import com.github.ai.leetcodequiz.api.{LoginResponse, SignupResponse}
import com.github.ai.leetcodequiz.data.db.model.{UserEntity, UserUid}
import com.github.ai.leetcodequiz.data.db.repository.UserRepository
import com.github.ai.leetcodequiz.data.protobuf.ProtobufSerializer
import com.github.ai.leetcodequiz.domain.PasswordService
import com.github.ai.leetcodequiz.domain.authentication.AuthService
import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.utils.{readBodyAsBytes, toUserDto}
import zio.*
import zio.direct.*
import zio.http.Request

import java.util.UUID

class AuthController(
  private val userRepository: UserRepository,
  private val passwordService: PasswordService,
  private val jwtService: AuthService,
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

    val token = jwtService.createToken(user.uid)
    SignupResponse(token, toUserDto(user))
  }

  def login(request: Request): IO[DomainError, LoginResponse] = defer {
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

    val token = jwtService.createToken(user.uid)
    LoginResponse(token, toUserDto(user))
  }
}
