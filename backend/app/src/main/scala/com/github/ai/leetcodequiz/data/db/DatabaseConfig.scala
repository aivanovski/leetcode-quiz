package com.github.ai.leetcodequiz.data.db

case class DatabaseConfig(
  url: String,
  user: String,
  password: String,
  maximumPoolSize: Int,
  minimumIdle: Int
)
