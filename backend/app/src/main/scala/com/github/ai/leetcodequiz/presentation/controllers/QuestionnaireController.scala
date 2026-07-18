package com.github.ai.leetcodequiz.presentation.controllers

import com.github.ai.leetcodequiz.api.PostSubmissionRequest
import com.github.ai.leetcodequiz.utils.{
  getLastUrlParameter,
  parseUid,
  readBodyAsBytes,
  toQuestionnaireItemDto,
  toQuestionnairesItemDto
}
import com.github.ai.leetcodequiz.api.{
  GetQuestionnaireResponse,
  GetQuestionnairesResponse,
  PostSubmissionResponse
}
import com.github.ai.leetcodequiz.data.db.model.{QuestionEntity, QuestionUid, QuestionnaireUid}
import com.github.ai.leetcodequiz.data.db.repository.{
  ProblemRepository,
  QuestionRepository,
  QuestionnaireRepository
}
import com.github.ai.leetcodequiz.domain.usecases.{
  CreateNewQuestionnaireUseCase,
  GetQuestionnaireStatsUseCase,
  SubmitQuestionAnswerUseCase
}
import com.github.ai.leetcodequiz.entity.Questionnaire
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.{IO, ZIO}
import zio.direct.*
import zio.http.{Request, Response}

import java.util.Random

class QuestionnaireController(
  private val createQuestionnaireUseCase: CreateNewQuestionnaireUseCase,
  private val submitAnswerUseCase: SubmitQuestionAnswerUseCase,
  private val getStatsUseCase: GetQuestionnaireStatsUseCase,
  private val problemRepository: ProblemRepository,
  private val questionnaireRepository: QuestionnaireRepository,
  private val questionRepository: QuestionRepository
) {

  def getQuestionnaire(
    request: Request
  ): IO[DomainError, GetQuestionnaireResponse] = defer {
    val uid = request
      .getLastUrlParameter()
      .flatMap(str => str.parseUid())
      .map(QuestionnaireUid(_))
      .run

    val questionnaire = questionnaireRepository.getByUid(uid).run
    val stats = getStatsUseCase.getStats(uid).run
    val questions = questionRepository.getAll().run

    val questionUidToQuestionMap = questions.map(q => (q.uid, q)).toMap

    val questionnaireDto = toQuestionnaireItemDto(
      questionnaire = questionnaire,
      stats = stats,
      questionUidToQuestionMap = questionUidToQuestionMap
    ).run

    GetQuestionnaireResponse(questionnaireDto)
  }

  def getQuestionnaires(): IO[DomainError, GetQuestionnairesResponse] = defer {
    val shouldCreateNew = createQuestionnaireUseCase.shouldCreateNewQuestionnaire().run
    if (shouldCreateNew) {
      createQuestionnaireUseCase.createNewQuestionnaire().run
    }

    val questionnaires = questionnaireRepository.getAll().run
    val questions = questionRepository.getAll().run
    val questionUidToQuestionMap = questions.map(q => (q.uid, q)).toMap

    val questionnairesAndStats = ZIO
      .collectAll(
        questionnaires
          .map { questionnaire =>
            getStatsUseCase
              .getStats(questionnaire.uid)
              .map(stats => (questionnaire, stats))
          }
      )
      .run

    val questionnaireDtos = ZIO
      .collectAll(
        questionnairesAndStats.map { (questionnaire, stats) =>
          toQuestionnairesItemDto(questionnaire)
        }
      )
      .run

    GetQuestionnairesResponse(questionnaireDtos)
  }

  private def getNextQuestions(
    questions: List[QuestionEntity]
  ): (QuestionUid, QuestionUid) = {
    val random = Random()

    val firstIndex = random.nextInt(questions.size)
    val secondIndex = random.nextInt(questions.size - 1)

    val (first, second) = if (secondIndex >= firstIndex) {
      (questions(firstIndex), questions(secondIndex + 1))
    } else {
      (questions(firstIndex), questions(secondIndex))
    }

    (first.uid, second.uid)
  }

  private def getActiveQuestionnaire(): IO[DomainError, Questionnaire] = defer {
    val result = questionnaireRepository.getAll().run
    val active = result.find(q => !q.isComplete)

    ZIO
      .fromOption(active)
      .mapError(_ => DomainError("Failed to find active questionnaire"))
      .run
  }
}
