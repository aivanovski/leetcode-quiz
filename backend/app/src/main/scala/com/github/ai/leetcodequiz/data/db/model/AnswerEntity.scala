package com.github.ai.leetcodequiz.data.db.model

case class AnswerEntity(
  uid: AnswerUid,
  questionnaireUid: QuestionnaireUid,
  questionUid: QuestionUid,
  answer: Int
)
