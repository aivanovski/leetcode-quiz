package com.github.ai.leetcodequiz.entity

import com.github.ai.leetcodequiz.data.db.model.UserEntity

case class AuthenticationContext(
  user: UserEntity
)
