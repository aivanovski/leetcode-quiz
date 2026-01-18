package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.doobie.model.{
  QuestionEntity,
  QuestionUid,
  QuestionnaireEntity,
  QuestionnaireUid
}
import com.github.ai.leetcodequiz.data.doobie.repository.{
  QuestionRepository,
  QuestionnaireRepository
}
import com.github.ai.leetcodequiz.entity.exception.DomainError

import java.util.{Random, UUID}
import zio.IO
import zio.direct.*

class CreateNewQuestionnaireUseCase(
  private val questionRepository: QuestionRepository,
  private val questionnaireRepository: QuestionnaireRepository
) {

  def shouldCreateNewQuestionnaire(): IO[DomainError, Boolean] = defer {
    val questionnaires = questionnaireRepository.getAll().run

    questionnaires.isEmpty || questionnaires.forall(q => q.isComplete)
  }

  def createNewQuestionnaire(): IO[DomainError, QuestionnaireEntity] = defer {
    val questions = questionRepository.getAll().run

    val (firstQuestionUid, secondQuestionUid) = getNextQuestions(questions)

    questionnaireRepository
      .add(
        QuestionnaireEntity(
          uid = QuestionnaireUid(UUID.randomUUID()),
          next = Some(firstQuestionUid),
          afterNext = Some(secondQuestionUid),
          isComplete = false,
          seed = 1L // TODO: remove seed
        )
      )
      .run
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
}
