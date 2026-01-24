package com.github.ai.leetcodequiz.data.db.model

case class NextQuestionEntity(
  uid: NextQuestionUid,
  questionnaireUid: QuestionnaireUid,
  questionUid: QuestionUid
)
