package com.github.ai.leetcodequiz.data.db.model

case class QuestionEntity(
  uid: QuestionUid,
  problemId: ProblemId,
  listType: ChallengeListType,
  question: String,
  complexity: String,
  formula: String,
  repeatability: Int,
  importance: Int
)

enum ChallengeListType {
  case BLIND_75, NEETCODE_150
}
