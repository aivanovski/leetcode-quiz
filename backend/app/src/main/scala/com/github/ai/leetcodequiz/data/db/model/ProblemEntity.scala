package com.github.ai.leetcodequiz.data.db.model

final case class ProblemEntity(
  id: ProblemId,
  title: String,
  content: String,
  category: String,
  url: String,
  difficulty: String,
  likes: Int,
  dislikes: Int
)
