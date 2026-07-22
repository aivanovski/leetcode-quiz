package com.github.ai.leetcodequiz.entity

case class JwtTokens(
  token: AuthToken,
  refreshToken: RefreshToken
)

enum JwtTokenType {
  case AUTH_TOKEN, REFRESH_TOKEN
}

object JwtTokenType {
  def fromString(name: String): Option[JwtTokenType] =
    JwtTokenType.values.find(token => token.toString == name)
}

opaque type AuthToken = String
opaque type RefreshToken = String

object AuthToken {
  def apply(token: String): AuthToken = token
}

object RefreshToken {
  def apply(refreshToken: String): RefreshToken = refreshToken
}
