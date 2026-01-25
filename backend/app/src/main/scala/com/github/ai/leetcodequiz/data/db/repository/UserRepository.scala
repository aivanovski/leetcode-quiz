package com.github.ai.leetcodequiz.data.db.repository

import com.github.ai.leetcodequiz.data.db.dao.UserEntityDao
import com.github.ai.leetcodequiz.data.db.model.{UserEntity, UserUid}

class UserRepository(
  private val dao: UserEntityDao
) {

  def getByUid(uid: UserUid) = dao.getByUid(uid)
  def findByUid(uid: UserUid) = dao.findByUid(uid)
  def findByEmail(email: String) = dao.findByEmail(email)
  def add(user: UserEntity) = dao.add(user)
  def delete(uid: UserUid) = dao.delete(uid)
}
