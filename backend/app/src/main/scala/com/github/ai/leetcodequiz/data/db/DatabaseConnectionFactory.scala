package com.github.ai.leetcodequiz.data.db

import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import com.github.ai.leetcodequiz.utils.toProperties
import com.typesafe.config.ConfigFactory
import zio.{IO, ZIO}
import zio.direct.*
import slick.jdbc.SQLiteProfile.api.*

class DatabaseConnectionFactory {

  def create(): IO[DatabaseError, Database] = defer {
    val config = loadConfig().run

    val db = ZIO
      .attempt {
        Database.forURL(
          url = config.url,
          user = config.user,
          password = config.password,
          driver = config.driverClassName,
          keepAliveConnection = true,
          prop = Map(
            "connectionPool" -> "HikariCP"
          ).toProperties()
        )
      }
      .mapError(DatabaseError(_))
      .run

    // TODO: maybe it would be better to use acquireRelease
//    val db = ZIO
//      .acquireRelease(
//        ZIO.attempt(
//          Database.forURL(
//            url = config.url,
//            user = config.user,
//            password = config.password,
//            driver = config.driverClassName,
//            keepAliveConnection = true,
//            prop = Map(
//              "connectionPool" -> "HikariCP"
//            ).toProperties()
//          )
//        )
//      )(db => ZIO.attempt(db.close()).orDie)
//      .mapError(DatabaseError(_))
//      .run

    db
  }

  private def loadConfig(): IO[DatabaseError, DatabaseConfig] = ZIO
    .attempt {
      val config = ConfigFactory.load().getConfig("db")
      val dataSource = config.getConfig("dataSource")
      val hikariConfig = config.getConfig("hikari")

      DatabaseConfig(
        url = dataSource.getString("url"),
        user = dataSource.getString("user"),
        password = dataSource.getString("password"),
        driverClassName = config.getString("driverClassName"),
        maximumPoolSize = hikariConfig.getInt("maximumPoolSize"),
        minimumIdle = hikariConfig.getInt("minimumIdle")
      )
    }
    .mapError(DatabaseError(_))
}
