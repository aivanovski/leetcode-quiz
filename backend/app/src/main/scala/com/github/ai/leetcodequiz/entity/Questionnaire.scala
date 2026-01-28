package com.github.ai.leetcodequiz.entity

import com.github.ai.leetcodequiz.data.db.model.{QuestionUid, QuestionnaireUid}

case class Questionnaire(
  uid: QuestionnaireUid,
  questions: List[QuestionUid],
  answers: List[Answer],
  isComplete: Boolean
)
