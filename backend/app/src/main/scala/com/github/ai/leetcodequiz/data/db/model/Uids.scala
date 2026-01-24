package com.github.ai.leetcodequiz.data.db.model

import doobie.util.meta.Meta

import java.util.UUID

opaque type SyncUid = UUID
opaque type QuestionUid = UUID
opaque type ProblemId = Long

opaque type QuestionnaireUid = UUID
opaque type NextQuestionUid = UUID
opaque type SubmissionUid = UUID
opaque type ProblemHintId = Long
opaque type UserUid = UUID

object QuestionnaireUid {
  def apply(uid: UUID): QuestionnaireUid = uid

  given Meta[QuestionnaireUid] =
    Meta[String].timap(str => QuestionnaireUid(UUID.fromString(str)))(_.toString)
}

object NextQuestionUid {
  def apply(uid: UUID): NextQuestionUid = uid

  given Meta[NextQuestionUid] =
    Meta[String].timap(str => NextQuestionUid(UUID.fromString(str)))(_.toString)
}

object SubmissionUid {
  def apply(uid: UUID): SubmissionUid = uid

  given Meta[SubmissionUid] =
    Meta[String].timap(str => SubmissionUid(UUID.fromString(str)))(_.toString)
}

object SyncUid {
  def apply(uid: UUID): SyncUid = uid

  given Meta[SyncUid] = Meta[String].timap(str => SyncUid(UUID.fromString(str)))(_.toString)
}

object QuestionUid {
  def apply(uid: UUID): QuestionUid = uid

  given Meta[QuestionUid] = Meta[String].timap(str => QuestionUid(UUID.fromString(str)))(_.toString)
}

object ProblemId {
  def apply(id: Long): ProblemId = id

  given Meta[ProblemId] = Meta[Long].timap(ProblemId(_))(identity)
}

object ProblemHintId {
  def apply(id: Long): ProblemHintId = id

  given Meta[ProblemHintId] = Meta[Long].timap(ProblemHintId(_))(identity)
}

object UserUid {
  def apply(uid: UUID): UserUid = uid

  given Meta[UserUid] =
    Meta[String].timap(str => UserUid(UUID.fromString(str)))(_.toString)
}
