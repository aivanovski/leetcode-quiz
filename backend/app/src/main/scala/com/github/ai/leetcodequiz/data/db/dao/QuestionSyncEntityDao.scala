package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.model.QuestionSyncEntity
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import io.getquill.SnakeCase
import zio.*
import io.getquill.jdbczio.Quill
import io.getquill.generic.*
import io.getquill.*

class QuestionSyncEntityDao(
  quill: Quill.H2[SnakeCase]
) {

  import quill._

  def getLastSync(): IO[DatabaseError, Option[QuestionSyncEntity]] = {
    val query = quote {
      querySchema[QuestionSyncEntity]("question_syncs")
        .sortBy(_.timestamp)(Ord.desc)
        .take(1)
    }

    run(query)
      .mapError(DatabaseError(_))
      .map(_.headOption)
  }

  def add(sync: QuestionSyncEntity): IO[DatabaseError, QuestionSyncEntity] = {
    run(
      quote {
        querySchema[QuestionSyncEntity]("question_syncs")
          .insertValue(lift(sync))
      }
    )
      .map(_ => sync)
      .mapError(DatabaseError(_))
  }

}
