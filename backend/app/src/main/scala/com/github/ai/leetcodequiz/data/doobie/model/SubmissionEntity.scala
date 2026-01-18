package com.github.ai.leetcodequiz.data.doobie.model

final case class SubmissionEntity(
  uid: SubmissionUid,
  questionnaireUid: QuestionnaireUid,
  questionUid: QuestionUid,
  answer: Int
)
