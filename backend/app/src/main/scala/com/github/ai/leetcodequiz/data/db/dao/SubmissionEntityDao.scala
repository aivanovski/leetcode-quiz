package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.data.db.execute
import com.github.ai.leetcodequiz.data.db.model.{
  QuestionUid,
  QuestionnaireUid,
  SubmissionEntity,
  SubmissionUid
}
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import doobie.implicits.*
import doobie.util.transactor.Transactor
import zio.{IO, Task}

class SubmissionEntityDao(
  private val transactor: Transactor[Task]
) {

  def getByQuestionnaireUid(
    questionnaireUid: QuestionnaireUid
  ): IO[DatabaseError, List[SubmissionEntity]] = {
    sql"""
        SELECT uid, questionnaire_uid, question_uid, answer
        FROM submissions
        WHERE questionnaire_uid = $questionnaireUid
      """
      .query[SubmissionEntity]
      .to[List]
      .execute(transactor)
  }

  def add(submission: SubmissionEntity): IO[DatabaseError, SubmissionEntity] = {
    sql"""
        INSERT INTO submissions (uid, questionnaire_uid, question_uid, answer)
        VALUES (${submission.uid}, ${submission.questionnaireUid}, ${submission.questionUid}, ${submission.answer})
      """.update.run
      .map(_ => submission)
      .execute(transactor)
  }
}
