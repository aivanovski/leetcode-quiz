package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.execute
import com.github.ai.leetcodequiz.data.db.model.{UserEntity, UserUid}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import doobie.implicits.*
import doobie.syntax.ConnectionIOOps
import doobie.util.transactor.Transactor
import zio.{IO, Task}

class UserEntityDao(
  private val transactor: Transactor[Task]
) {

  def findByUid(uid: UserUid): IO[DatabaseError, Option[UserEntity]] = {
    sql"""
          SELECT uid, name, email, password_hash
          FROM users
          WHERE uid = ${uid.toString}
        """
      .query[UserEntity]
      .option
      .execute(transactor)
  }

  def findByEmail(email: String): IO[DatabaseError, Option[UserEntity]] = {
    sql"""
        SELECT uid, name, email, password_hash
        FROM users
        WHERE email = $email
      """
      .query[UserEntity]
      .option
      .execute(transactor)
  }

  def add(user: UserEntity): IO[DatabaseError, UserEntity] = {
    sql"""
        INSERT INTO users (uid, name, email, password_hash)
        VALUES (${user.uid}, ${user.name}, ${user.email}, ${user.passwordHash})
      """.update.run
      .map(_ => user)
      .execute(transactor)
  }

  def delete(uid: UserUid): IO[DatabaseError, Unit] = {
    sql"""
        DELETE FROM users
        WHERE uid = $uid
      """.update.run
      .execute(transactor)
      .unit
  }
}
