package com.github.ai.leetcodequiz.data.doobie.model

import doobie.util.meta.Meta

import java.util.UUID

opaque type SyncUid = UUID
opaque type QuestionUid = UUID
opaque type ProblemId = Long

opaque type QuestionnaireUid = UUID
opaque type SubmissionUid = UUID
opaque type ProblemHintId = Long

object QuestionnaireUid {
  def apply(uid: UUID): QuestionnaireUid = uid

  given Meta[QuestionnaireUid] =
    Meta[String].timap(str => QuestionnaireUid(UUID.fromString(str)))(_.toString)
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
