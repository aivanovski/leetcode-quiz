package com.github.ai.leetcodequiz.data.db.model

final case class QuestionEntity(
  uid: QuestionUid,
  problemId: ProblemId,
  question: String,
  complexity: String
)
