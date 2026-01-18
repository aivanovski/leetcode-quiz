package com.github.ai.leetcodequiz.data.db

import com.typesafe.config.{Config, ConfigFactory}
import doobie.hikari.{Config as HikariConfig, HikariTransactor}
import doobie.implicits.*
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor

import scala.io.Source
import scala.util.Using
import zio.*
import zio.interop.catz.*

case class DatabaseConfig(
  config: HikariConfig
)

object DoobieTransactor {

  private val initScriptResource = "init-sqlite.sql"

  private def loadConfig(prefix: String): Task[DatabaseConfig] = ZIO.attempt {
    val config = ConfigFactory.load().getConfig(prefix)
    val dataSource = config.getConfig("dataSource")

    DatabaseConfig(
      config = HikariConfig(
        jdbcUrl = dataSource.getString("url"),
        username = Some(dataSource.getString("user")),
        password = Some(dataSource.getString("password")),
        poolName = Some(s"$prefix-hikari-pool"),
        driverClassName = Some(config.getString("driverClassName"))
      )
    )
  }

  private def loadInitStatements(): Task[List[String]] = ZIO.attempt {
    val sql = Using.resource(Source.fromResource(initScriptResource))(_.mkString)
    sql.split(";").toList.map(_.trim).filter(_.nonEmpty)
  }

  def layer(prefix: String): ZLayer[Any, Throwable, Transactor[Task]] = {
    ZLayer.scoped {
      for {
        dbConfig <- loadConfig(prefix)
        xa <- HikariTransactor.fromConfig[Task](dbConfig.config).toScopedZIO
        _ <- loadInitStatements().flatMap { statements =>
          ZIO.foreachDiscard(statements) { statement =>
            Fragment.const(statement).update.run.transact(xa)
          }
        }
      } yield xa
    }
  }
}
