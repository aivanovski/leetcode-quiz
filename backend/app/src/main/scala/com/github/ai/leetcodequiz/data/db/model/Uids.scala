package com.github.ai.leetcodequiz.data.db.model

import java.util.UUID

opaque type SyncUid = UUID
opaque type QuestionUid = UUID
opaque type ProblemId = Long

opaque type QuestionnaireUid = UUID
opaque type NextQuestionUid = UUID
opaque type AnswerUid = UUID
opaque type ProblemHintId = Long
opaque type UserUid = UUID
opaque type SolutionUid = UUID

object QuestionnaireUid {
  def apply(uid: UUID): QuestionnaireUid = uid
}

object NextQuestionUid {
  def apply(uid: UUID): NextQuestionUid = uid
}

object AnswerUid {
  def apply(uid: UUID): AnswerUid = uid
}

object SyncUid {
  def apply(uid: UUID): SyncUid = uid
}

object QuestionUid {
  def apply(uid: UUID): QuestionUid = uid
}

object ProblemId {
  def apply(id: Long): ProblemId = id

  extension (id: ProblemId) inline def value: Long = id

  given Ordering[ProblemId] = Ordering.Long
}

object ProblemHintId {
  def apply(id: Long): ProblemHintId = id

  extension (id: ProblemHintId) inline def value: Long = id
}

object UserUid {
  def apply(uid: UUID): UserUid = uid
}

object SolutionUid {
  def apply(uid: UUID): SolutionUid = uid
}
