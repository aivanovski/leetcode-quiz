package com.github.ai.leetcodequiz.presentation.controllers

import com.github.ai.leetcodequiz.apisc.request.{LoginRequest, SignupRequest}
import com.github.ai.leetcodequiz.apisc.response.{LoginResponse, SignupResponse}
import com.github.ai.leetcodequiz.data.db.model.{UserEntity, UserUid}
import com.github.ai.leetcodequiz.data.db.repository.UserRepository
import com.github.ai.leetcodequiz.data.json.JsonSerializer
import com.github.ai.leetcodequiz.domain.PasswordService
import com.github.ai.leetcodequiz.domain.authentication.JwtTokeService
import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.utils.{readBodyAsString, toUserDto}
import zio.*
import zio.direct.*
import zio.http.{Request, Response}

import java.util.UUID

class AuthController(
  private val userRepository: UserRepository,
  private val passwordService: PasswordService,
  private val jwtService: JwtTokeService,
  private val jsonSerializer: JsonSerializer
) {

  def signup(request: Request): IO[DomainError, Response] = defer {
    val body = request
      .readBodyAsString()
      .flatMap { text => jsonSerializer.deserialize[SignupRequest](text) }
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
    val response = SignupResponse(token, toUserDto(user))

    Response.json(jsonSerializer.serialize(response))
  }

  def login(request: Request): IO[DomainError, Response] = defer {
    val body = request
      .readBodyAsString()
      .flatMap { text => jsonSerializer.deserialize[LoginRequest](text) }
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
    val response = LoginResponse(token, toUserDto(user))

    Response.json(jsonSerializer.serialize(response))
  }
}
