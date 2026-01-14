package com.github.ai.leetcodequiz.data.db.model

import io.getquill.MappedEncoding

import java.util.UUID

opaque type UserUid = UUID

object UserUid {
  def apply(uid: UUID): UserUid = uid

  implicit val encodeUserUid: MappedEncoding[UserUid, UUID] = MappedEncoding[UserUid, UUID](identity)
  implicit val decodeUserUid: MappedEncoding[UUID, UserUid] = MappedEncoding[UUID, UserUid](UserUid(_))
}