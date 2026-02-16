package com.github.ai.leetcodequiz.entity

case class JwtConfig(
  secret: String,
  issuer: String,
  audience: String,
  realm: String
)
