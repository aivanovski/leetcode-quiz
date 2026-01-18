package com.github.ai.leetcodequiz.data.doobie.model

final case class ProblemHintEntity(
  id: ProblemHintId,
  problemId: ProblemId,
  hint: String
)
