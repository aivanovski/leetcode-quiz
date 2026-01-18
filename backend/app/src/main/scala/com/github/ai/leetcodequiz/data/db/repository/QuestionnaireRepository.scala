package com.github.ai.leetcodequiz.data.db.repository

import com.github.ai.leetcodequiz.data.db.dao.QuestionnaireEntityDao
import com.github.ai.leetcodequiz.data.db.model.{QuestionnaireEntity, QuestionnaireUid}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.ZIO

class QuestionnaireRepository(
  private val dao: QuestionnaireEntityDao
) {

  def getAll() = dao.getAll()

  def getByUid(uid: QuestionnaireUid) = {
    dao
      .getByUid(uid)
      .flatMap { opt =>
        ZIO.fromOption(opt).mapError(_ => DomainError(""))
      }
  }

  def add(questionnaire: QuestionnaireEntity) = dao.add(questionnaire)
  def update(questionnaire: QuestionnaireEntity) = dao.update(questionnaire)
}
