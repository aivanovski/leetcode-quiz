package com.github.ai.leetcodequiz.data.db.model

import doobie.util.meta.Meta

enum SyncType {
  case PROBLEMS, QUESTIONS
}

object SyncType {
  given Meta[SyncType] = Meta[String].timap(SyncType.valueOf)(_.toString)
}
