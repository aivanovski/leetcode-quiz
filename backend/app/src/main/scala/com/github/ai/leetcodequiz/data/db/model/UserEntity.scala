package com.github.ai.leetcodequiz.data.db.model

case class UserEntity(
  uid: UserUid,
  name: String
)

object UserEntity {
  inline val TableName = "users"
}
