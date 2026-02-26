package com.github.ai.leetcodequiz.data.db.model

case class SolutionEntity(
  uid: SolutionUid,
  problemId: ProblemId,
  path: String,
  content: String,
  sourceType: SourceType
)

enum SourceType {
  case PERSONAL, EXTERNAL
}
