package com.github.ai.leetcodequiz.entity

import com.github.ai.leetcodequiz.data.db.model.ProblemId

case class Problem(
  id: ProblemId,
  title: String,
  content: String,
  category: String,
  url: String,
  difficulty: Difficulty,
  hints: List[String],
  likes: Int,
  dislikes: Int
//  stats: ProblemStats,
)
