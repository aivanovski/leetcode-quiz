package com.github.ai.leetcodequiz.data.doobie

import com.typesafe.config.ConfigFactory
import doobie.implicits.*
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor

import java.util.Properties
import scala.io.Source
import scala.util.Using
import zio.*
import zio.interop.catz.*

final case class DoobieDbConfig(
  driverClassName: String,
  url: String,
  user: String,
  password: String
)

object DoobieTransactor {

  private val initScriptResource = "init-sqlite.sql"

  private def loadConfig(prefix: String): Task[DoobieDbConfig] = ZIO.attempt {
    val config = ConfigFactory.load().getConfig(prefix)
    val dataSource = config.getConfig("dataSource")

    DoobieDbConfig(
      driverClassName = config.getString("driverClassName"),
      url = dataSource.getString("url"),
      user = dataSource.getString("user"),
      password = dataSource.getString("password")
    )
  }

  private def loadInitStatements(): Task[List[String]] = ZIO.attempt {
    val sql = Using.resource(Source.fromResource(initScriptResource))(_.mkString)
    sql.split(";").toList.map(_.trim).filter(_.nonEmpty)
  }

  def layer(prefix: String): ZLayer[Any, Throwable, Transactor[Task]] = {
    ZLayer.scoped {
      for {
        cfg <- loadConfig(prefix)
        xa <- ZIO.attempt {
          val props = new Properties()

          props.setProperty("user", cfg.user)
          props.setProperty("password", cfg.password)

          Transactor.fromDriverManager[Task](
            cfg.driverClassName,
            cfg.url,
            props,
            None
          )
        }
        _ <- loadInitStatements().flatMap { statements =>
          ZIO.foreachDiscard(statements) { statement =>
            Fragment.const(statement).update.run.transact(xa)
          }
        }
      } yield xa
    }
  }
}
