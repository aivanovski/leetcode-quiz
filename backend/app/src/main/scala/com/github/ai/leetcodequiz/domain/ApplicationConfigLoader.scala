package com.github.ai.leetcodequiz.domain

import com.github.ai.leetcodequiz.data.db.DatabaseConfig
import com.github.ai.leetcodequiz.entity.exception.{DomainError, EnvironmentError}
import com.github.ai.leetcodequiz.entity.*
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.internal.DotenvReader
import zio.*
import zio.direct.*

class ApplicationConfigLoader {

  def loadConfig(): IO[DomainError, ApplicationConfig] = defer {
    val dotenv = Dotenv.configure().ignoreIfMissing().systemProperties().load()

    val environment = readRequired(dotenv, "ENVIRONMENT")
      .flatMap(value => parseEnvrironment(value))
      .run
    val protocol = readRequired(dotenv, "PROTOCOL")
      .flatMap(value => parseProtocol(value))
      .run

    val dbUrl = readRequired(dotenv, "DATABASE_URL").run
    val dbUser = readRequired(dotenv, "DATABASE_USER").run
    val dbPassword = readRequired(dotenv, "DATABASE_PASSWORD").run
    val dbMaximumPoolSize = readIntRequired(dotenv, "DATABASE_MAX_POOL_SIZE").run
    val dbMinimumIdle = readIntRequired(dotenv, "DATABASE_MIN_IDLE").run

    val jwtSecret = readRequired(dotenv, "JWT_SECRET").run
    val jwtIssuer = readRequired(dotenv, "JWT_ISSUER").run
    val jwtAudience = readRequired(dotenv, "JWT_AUDIENCE").run
    val jwtRealm = readRequired(dotenv, "JWT_REALM").run

    val debugUsers = readOptional(dotenv, "DEBUG_USERS")
      .map(users => parseCsv(users))
      .run
    val debugPasswords = readOptional(dotenv, "DEBUG_PASSWORDS")
      .map(passwords => parseCsv(passwords))
      .run

    ApplicationConfig(
      environment = environment,
      server = ServerConfig(
        protocol = protocol
      ),
      database = DatabaseConfig(
        url = dbUrl,
        user = dbUser,
        password = dbPassword,
        maximumPoolSize = dbMaximumPoolSize,
        minimumIdle = dbMinimumIdle
      ),
      jwt = JwtConfig(
        secret = jwtSecret,
        issuer = jwtIssuer,
        audience = jwtAudience,
        realm = jwtRealm
      ),
      debugUsers = debugUsers
        .zip(debugPasswords)
        .map((email, password) => DebugUserCredential(email = email, password = password))
    )
  }

  private def readOptional(dotenv: Dotenv, key: String): IO[EnvironmentError, Option[String]] =
    ZIO.succeed(Option(dotenv.get(key)).map(_.trim).filter(_.nonEmpty))

  private def readRequired(dotenv: Dotenv, key: String): IO[EnvironmentError, String] =
    readOptional(dotenv, key)
      .flatMap(ZIO.fromOption(_))
      .mapError(_ => EnvironmentError(s"Missing required .env value: $key"))

  private def parseProtocol(value: String): IO[EnvironmentError, HttpProtocol] =
    ZIO
      .fromOption(HttpProtocol.fromString(value))
      .mapError(_ => EnvironmentError(s"Invalid PROTOCOL '$value'. Expected 'http' or 'https'"))

  private def parseEnvrironment(value: String): IO[EnvironmentError, AppEnvironment] =
    ZIO
      .attempt(AppEnvironment.valueOf(value.toUpperCase()))
      .mapError(_ => EnvironmentError(s"Invalid ENVIRONMENT value: $value"))

  private def readIntRequired(dotenv: Dotenv, key: String): IO[EnvironmentError, Int] = defer {
    val value = readRequired(dotenv, key).run

    ZIO
      .fromOption(value.toIntOption)
      .mapError(_ => EnvironmentError(s"Invalid integer value in .env variable: $key=$value"))
      .run
  }

  private def parseCsv(value: Option[String]): List[String] =
    value.toList
      .flatMap(_.split(","))
      .map(_.trim)
      .filter(_.nonEmpty)

}
