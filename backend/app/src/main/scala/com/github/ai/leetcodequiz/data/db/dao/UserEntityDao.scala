package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.{AppDatabase, SlickMappers}
import com.github.ai.leetcodequiz.data.db.model.{UserEntity, UserUid}
import com.github.ai.leetcodequiz.entity.exception.{DatabaseError, FailedToFindEntityError}
import slick.jdbc.SQLiteProfile.api.*
import zio.*
import zio.direct.*

class UserEntityDao(
  db: AppDatabase
) extends Dao(db = db.context, table = db.UsersTable) {

  import SlickMappers.given

  def getByUid(uid: UserUid): IO[DatabaseError, UserEntity] = defer {
    findByUid(uid).run match
      case Some(user) => user
      case None =>
        ZIO
          .fail(FailedToFindEntityError(entityType = classOf[UserEntity], criteria = s"uid = $uid"))
          .run
  }

  def findByUid(uid: UserUid): IO[DatabaseError, Option[UserEntity]] =
    queryOne(_.uid === uid)

  def findByEmail(email: String): IO[DatabaseError, Option[UserEntity]] =
    queryOne(_.email === email)

  def add(user: UserEntity): IO[DatabaseError, UserEntity] =
    insert(user)

  def deleteUser(uid: UserUid): IO[DatabaseError, Unit] =
    deleteOne(_.uid === uid)
}
