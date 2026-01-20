package com.github.ai.leetcodequiz.presentation.controllers

import com.github.ai.leetcodequiz.apisc.request.PostSubmissionRequest
import com.github.ai.leetcodequiz.utils.{getLastUrlParameter, parseUid, readBodyAsString, toQuestionnaireItemDto}
import com.github.ai.leetcodequiz.apisc.response.{GetQuestionnaireResponse, GetQuestionnairesResponse, PostSubmissionResponse}
import com.github.ai.leetcodequiz.data.db.model.{QuestionEntity, QuestionUid, QuestionnaireEntity, QuestionnaireUid}
import com.github.ai.leetcodequiz.data.db.repository.{QuestionRepository, QuestionnaireRepository, SubmissionRepository}
import com.github.ai.leetcodequiz.data.json.JsonSerializer
import com.github.ai.leetcodequiz.domain.usecases.{CreateNewQuestionnaireUseCase, SubmitQuestionAnswerUseCase}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.{IO, ZIO}
import zio.direct.*
import zio.http.{Request, Response}

import java.util.Random

class QuestionnaireController(
  private val createQuestionnaireUseCase: CreateNewQuestionnaireUseCase,
  private val submitAnswerUseCase: SubmitQuestionAnswerUseCase,
  private val questionnaireRepository: QuestionnaireRepository,
  private val submissionRepository: SubmissionRepository,
  private val questionRepository: QuestionRepository,
  private val jsonSerializer: JsonSerializer
) {

  def getQuestionnaire(
    request: Request
  ): IO[DomainError, Response] = defer {
    val uid = request
      .getLastUrlParameter()
      .flatMap(str => str.parseUid())
      .map(QuestionnaireUid(_))
      .run

    val questionnaire = questionnaireRepository.getByUid(uid).run
    val questionUidToQuestionMap = questionRepository
      .getAll()
      .run
      .map(q => (q.uid, q))
      .toMap

    val response = toQuestionnaireItemDto(
      questionnaire = questionnaire,
      questionUidToQuestionMap = questionUidToQuestionMap
    ).run

    Response.json(jsonSerializer.serialize(GetQuestionnaireResponse(response)))
  }

  def getQuestionnaires(): IO[DomainError, Response] = defer {
    val shouldCreateNew = createQuestionnaireUseCase.shouldCreateNewQuestionnaire().run
    if (shouldCreateNew) {
      createQuestionnaireUseCase.createNewQuestionnaire().run
    }

    val questionnaires = questionnaireRepository.getAll().run
    val questionnaire = getActiveQuestionnaire().run
    val questions = questionRepository.getAll().run

    val submissions = submissionRepository.getByQuestionnaireUid(questionnaire.uid).run

    val answeredQuestions = submissions.map(s => s.questionUid).toSet
    val activeQuestions = questions.filter(q => !answeredQuestions.contains(q.uid))

    val response = createResponse(
      questionnaires = questionnaires,
      questions = questions
    ).run

    Response.json(jsonSerializer.serialize(response))
  }

  def postSubmission(
    request: Request
  ): IO[DomainError, Response] = defer {
    val body = request
      .readBodyAsString()
      .flatMap { text => jsonSerializer.deserialize[PostSubmissionRequest](text) }
      .run

    val questionnaireUid = request
      .getLastUrlParameter()
      .flatMap(str => str.parseUid())
      .map(QuestionnaireUid(_))
      .run

    val questionUid = body.questionId.parseUid().map(QuestionUid(_)).run

    val questionnaire = submitAnswerUseCase
      .submitAnswer(
        questionnaireUid = questionnaireUid,
        questionUid = questionUid,
        answer = body.answer
      )
      .run

    val questionUidToQuestionMap = questionRepository
      .getAll()
      .run
      .map(q => (q.uid, q))
      .toMap

    val response = toQuestionnaireItemDto(
      questionnaire = questionnaire,
      questionUidToQuestionMap = questionUidToQuestionMap
    ).run

    Response.json(jsonSerializer.serialize(PostSubmissionResponse(response)))
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

  private def getActiveQuestionnaire(): IO[DomainError, QuestionnaireEntity] = defer {
    val result = questionnaireRepository.getAll().run
    val active = result.find(q => !q.isComplete)

    ZIO
      .fromOption(active)
      .mapError(_ => DomainError("Failed to find active questionnaire"))
      .run
  }

  private def createResponse(
    questionnaires: List[QuestionnaireEntity],
    questions: List[QuestionEntity]
  ): IO[DomainError, GetQuestionnairesResponse] = {
    for {
      items <- ZIO
        .collectAll(
          questionnaires.map { questionnaire =>
            toQuestionnaireItemDto(
              questionnaire = questionnaire,
              questionUidToQuestionMap = questions.map(q => (q.uid, q)).toMap
            )
          }
        )
    } yield GetQuestionnairesResponse(items)
  }
}
