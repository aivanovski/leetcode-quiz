package com.github.ai.leetcodequiz.data.db.repository

import com.github.ai.leetcodequiz.data.db.dao.SubmissionEntityDao
import com.github.ai.leetcodequiz.data.db.model.{SubmissionEntity, QuestionnaireUid}

class SubmissionRepository(
  private val dao: SubmissionEntityDao
) {
  def getByQuestionnaireUid(questionnaireUid: QuestionnaireUid) =
    dao.getByQuestionnaireUid(questionnaireUid)
  def add(submission: SubmissionEntity) = dao.add(submission)
}
