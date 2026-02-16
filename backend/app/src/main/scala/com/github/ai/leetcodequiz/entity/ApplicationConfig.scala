package com.github.ai.leetcodequiz.entity

import com.github.ai.leetcodequiz.data.db.DatabaseConfig
import zio.{Task, ZIO, ZLayer}

case class ApplicationConfig(
  environment: AppEnvironment,
  server: ServerConfig,
  database: DatabaseConfig,
  jwt: JwtConfig,
  debugUsers: List[DebugUserCredential]
)

case class ServerConfig(protocol: HttpProtocol)
case class DebugUserCredential(email: String, password: String)
