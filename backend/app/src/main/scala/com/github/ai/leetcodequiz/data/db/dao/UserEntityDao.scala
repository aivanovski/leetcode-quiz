package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.model.{UserEntity, UserUid}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.utils.toDomainError
import com.github.ai.leetcodequiz.utils.some
import io.getquill.jdbczio.Quill
import io.getquill.generic.*
import io.getquill.*
import zio.*

class UserEntityDao(
  quill: Quill.H2[SnakeCase]
) {

  import quill._

  // TODO: refactor
  def getAll(): IO[DomainError, List[UserEntity]] = {
    val query = quote {
      querySchema[UserEntity]("users")
    }

    run(query)
      .mapError(_.toDomainError())
  }

  def findByUid(uid: UserUid): IO[DomainError, Option[UserEntity]] = {
    val query = quote {
      querySchema[UserEntity]("users")
        .filter(_.uid == lift(uid))
    }

    for {
      users <- run(query).mapError(_.toDomainError())
    } yield users.headOption
  }

  def getByUids(uids: List[UserUid]): IO[DomainError, List[UserEntity]] = {
    val uidSet = uids.toSet

    val query = quote {
      querySchema[UserEntity]("users")
        .filter(usr => liftQuery(uidSet).contains(usr.uid))
    }

    for {
      users <- run(query).mapError(_.toDomainError())
      _ <-
        if (users.size != uids.size) {
          val notFoundUids = users
            .map(_.uid)
            .filter(uid => !uids.contains(uid))
            .mkString(", ")

          ZIO.fail(DomainError(message = s"Failed to find users: $notFoundUids".some))
        } else {
          ZIO.succeed(())
        }
    } yield users
  }

  def getByUid(uid: UserUid): IO[DomainError, UserEntity] = {
    val query = quote {
      querySchema[UserEntity]("users")
        .filter(_.uid == lift(uid))
    }

    for {
      users <- run(query).mapError(_.toDomainError())
      user <-
        if (users.nonEmpty) {
          ZIO.succeed(users.head)
        } else {
          ZIO.fail(DomainError(message = s"Failed to find user by uid: $uid".some))
        }
    } yield user
  }

  def add(user: UserEntity): IO[DomainError, UserEntity] = {
    run(
      quote {
        querySchema[UserEntity]("users")
          .insertValue(lift(user))
      }
    )
      .map(_ => user)
      .mapError(_.toDomainError())
  }

  def update(user: UserEntity): IO[DomainError, UserEntity] = {
    val updateQuery = quote {
      querySchema[UserEntity]("users")
        .filter(_.uid == lift(user.uid))
        .updateValue(lift(user))
    }

    run(updateQuery)
      .map(_ => user)
      .mapError(_.toDomainError())
  }

  // TODO: remove function and refactor
  def getUserUidToUserMap(): IO[DomainError, Map[UserUid, UserEntity]] = {
    for {
      users <- getAll()
    } yield {
      users.map { user => user.uid -> user }.toMap
    }
  }
}

object UserEntityDao {
  val TableName = "users"
}
