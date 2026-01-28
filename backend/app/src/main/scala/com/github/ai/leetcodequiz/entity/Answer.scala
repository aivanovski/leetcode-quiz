package com.github.ai.leetcodequiz.entity

import com.github.ai.leetcodequiz.data.db.model.QuestionUid

case class Answer(
  uid: QuestionUid,
  answer: Int
)
