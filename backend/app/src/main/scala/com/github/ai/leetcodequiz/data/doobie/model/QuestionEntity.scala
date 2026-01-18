package com.github.ai.leetcodequiz.data.doobie.model

final case class QuestionEntity(
  uid: QuestionUid,
  problemId: ProblemId,
  question: String,
  complexity: String
)
