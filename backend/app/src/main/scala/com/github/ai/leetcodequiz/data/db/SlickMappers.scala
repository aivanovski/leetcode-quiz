package com.github.ai.leetcodequiz.data.db

import com.github.ai.leetcodequiz.data.db.model.*
import slick.jdbc.SQLiteProfile.api.*

import java.time.LocalDateTime
import java.util.UUID

object SlickMappers {

  given BaseColumnType[QuestionnaireUid] =
    MappedColumnType.base[QuestionnaireUid, String](
      _.toString,
      str => QuestionnaireUid(UUID.fromString(str))
    )

  given BaseColumnType[NextQuestionUid] =
    MappedColumnType.base[NextQuestionUid, String](
      _.toString,
      str => NextQuestionUid(UUID.fromString(str))
    )

  given BaseColumnType[AnswerUid] =
    MappedColumnType.base[AnswerUid, String](_.toString, str => AnswerUid(UUID.fromString(str)))

  given BaseColumnType[SyncUid] =
    MappedColumnType.base[SyncUid, String](_.toString, str => SyncUid(UUID.fromString(str)))

  given BaseColumnType[QuestionUid] =
    MappedColumnType.base[QuestionUid, String](_.toString, str => QuestionUid(UUID.fromString(str)))

  given BaseColumnType[ProblemId] =
    MappedColumnType.base[ProblemId, Long](id => id.value, value => ProblemId(value))

  given BaseColumnType[ProblemHintId] =
    MappedColumnType.base[ProblemHintId, Long](id => id.value, value => ProblemHintId(value))

  given BaseColumnType[UserUid] =
    MappedColumnType.base[UserUid, String](_.toString, str => UserUid(UUID.fromString(str)))

  given BaseColumnType[SolutionUid] =
    MappedColumnType.base[SolutionUid, String](_.toString, str => SolutionUid(UUID.fromString(str)))

  given BaseColumnType[SyncType] =
    MappedColumnType.base[SyncType, String](_.toString, SyncType.valueOf)

  given BaseColumnType[ChallengeListType] =
    MappedColumnType.base[ChallengeListType, String](_.toString, ChallengeListType.valueOf)

  given BaseColumnType[SourceType] =
    MappedColumnType.base[SourceType, String](_.toString, SourceType.valueOf)

  given BaseColumnType[LocalDateTime] =
    MappedColumnType.base[LocalDateTime, String](_.toString, LocalDateTime.parse)

  given BaseColumnType[Boolean] =
    MappedColumnType.base[Boolean, Int](bool => if (bool) 1 else 0, value => value != 0)
}
