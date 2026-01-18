package com.github.ai.leetcodequiz.data.doobie

import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import doobie.syntax.ConnectionIOOps
import doobie.util.meta.Meta
import doobie.util.transactor.Transactor
import zio.*
import zio.interop.catz.*

import java.time.LocalDateTime

// Meta instance for LocalDateTime (stored as TEXT in SQLite)
given Meta[LocalDateTime] = Meta[String].timap(LocalDateTime.parse)(_.toString)

// Meta instance for Boolean (stored as INTEGER in SQLite: 0=false, 1=true)
given Meta[Boolean] = Meta[Int].timap(_ != 0)(if (_) 1 else 0)

extension [A](connection: ConnectionIOOps[A]) {
  def execute(transactor: Transactor[Task]): IO[DatabaseError, A] = {
    connection
      .transact(transactor)
      .mapError(DatabaseError(_))
  }
}
