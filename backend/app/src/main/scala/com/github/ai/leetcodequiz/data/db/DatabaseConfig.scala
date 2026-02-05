package com.github.ai.leetcodequiz.data.db

case class DatabaseConfig(
  url: String,
  user: String,
  password: String,
  driverClassName: String,
  maximumPoolSize: Int,
  minimumIdle: Int
)
