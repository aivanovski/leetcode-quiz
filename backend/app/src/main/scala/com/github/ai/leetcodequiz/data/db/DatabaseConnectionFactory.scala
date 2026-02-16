package com.github.ai.leetcodequiz.data.db

import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import zio.{Scope, ZIO}
import zio.direct.*
import slick.jdbc.SQLiteProfile.api.*

class DatabaseConnectionFactory(
  private val config: DatabaseConfig
) {

  def create(): ZIO[Scope, DatabaseError, Database] = defer {
    val db =
      ZIO
        .acquireRelease {
          ZIO.attempt {
            val hikariConfig = new HikariConfig()
            hikariConfig.setJdbcUrl(config.url)
            hikariConfig.setUsername(config.user)
            hikariConfig.setPassword(config.password)
            hikariConfig.setDriverClassName("org.sqlite.JDBC")
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
}
