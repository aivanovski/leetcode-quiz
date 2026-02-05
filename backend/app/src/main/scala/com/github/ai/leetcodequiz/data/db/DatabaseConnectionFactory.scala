package com.github.ai.leetcodequiz.data.db

import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import zio.{IO, Scope, ZIO}
import zio.direct.*
import slick.jdbc.SQLiteProfile.api.*

class DatabaseConnectionFactory {

  def create(): ZIO[Scope, DatabaseError, Database] = defer {
    val config = loadConfig().run

    val db =
      ZIO
        .acquireRelease {
          ZIO.attempt {
            val hikariConfig = new HikariConfig()
            hikariConfig.setJdbcUrl(config.url)
            hikariConfig.setUsername(config.user)
            hikariConfig.setPassword(config.password)
            hikariConfig.setDriverClassName(config.driverClassName)
            hikariConfig.setMaximumPoolSize(config.maximumPoolSize)
            hikariConfig.setMinimumIdle(config.minimumIdle)

            val dataSource = new HikariDataSource(hikariConfig)
            Database.forDataSource(dataSource, Some(config.maximumPoolSize))
          }
        }(db => ZIO.attempt(db.close()).orDie)
        .mapError(DatabaseError(_))
        .run

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
