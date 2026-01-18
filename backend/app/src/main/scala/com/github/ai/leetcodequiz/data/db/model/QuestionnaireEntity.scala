package com.github.ai.leetcodequiz.data.db.model

final case class QuestionnaireEntity(
  uid: QuestionnaireUid,
  next: Option[QuestionUid],
  afterNext: Option[QuestionUid],
  isComplete: Boolean,
  seed: Long
)
