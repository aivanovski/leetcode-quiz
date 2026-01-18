package com.github.ai.leetcodequiz.data.db.model

final case class SubmissionEntity(
  uid: SubmissionUid,
  questionnaireUid: QuestionnaireUid,
  questionUid: QuestionUid,
  answer: Int
)
