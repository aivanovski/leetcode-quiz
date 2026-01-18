package com.github.ai.leetcodequiz.data.db.model

final case class ProblemHintEntity(
  id: ProblemHintId,
  problemId: ProblemId,
  hint: String
)
