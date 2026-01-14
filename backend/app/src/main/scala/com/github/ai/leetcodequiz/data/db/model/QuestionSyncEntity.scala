package com.github.ai.leetcodequiz.data.db.model

import java.time.LocalDateTime
import java.util.UUID

case class QuestionSyncEntity(
  uid: UUID,
  timestamp: LocalDateTime
)
