package com.github.ai.leetcodequiz.data.doobie.repository

import com.github.ai.leetcodequiz.data.doobie.dao.SubmissionEntityDao
import com.github.ai.leetcodequiz.data.doobie.model.{SubmissionEntity, QuestionnaireUid}

class SubmissionRepository(
  private val dao: SubmissionEntityDao
) {
  def getByQuestionnaireUid(questionnaireUid: QuestionnaireUid) =
    dao.getByQuestionnaireUid(questionnaireUid)
  def add(submission: SubmissionEntity) = dao.add(submission)
}
