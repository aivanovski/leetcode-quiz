package com.github.ai.leetcodequiz.utils

import com.github.ai.leetcodequiz.api.{QuestionItemDto, QuestionnaireItemDto}
import com.github.ai.leetcodequiz.data.doobie.model.{
  QuestionEntity,
  QuestionUid,
  QuestionnaireEntity
}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

def toQuestionnaireItemDto(
  questionnaire: QuestionnaireEntity,
  questionUidToQuestionMap: Map[QuestionUid, QuestionEntity]
): IO[DomainError, QuestionnaireItemDto] = defer {
  val nextQuestion = resolveQuestion(questionnaire.next, questionUidToQuestionMap).run
  val afterNextQuestion = resolveQuestion(questionnaire.afterNext, questionUidToQuestionMap).run

  val questions = List(nextQuestion, afterNextQuestion).flatten
    .map { q =>
      QuestionItemDto(
        id = q.uid.toString,
        problemId = q.problemId.toString.toInt,
        problemTitle = "TO BE DONE", // TODO
        question = q.question,
        complexity = q.complexity
      )
    }

  createQuestionnairesItemDto(
    questionnaire = questionnaire,
    questions = questions
  )
}

private def createQuestionnairesItemDto(
  questionnaire: QuestionnaireEntity,
  questions: List[QuestionItemDto]
): QuestionnaireItemDto = {
  QuestionnaireItemDto(
    id = questionnaire.uid.toString,
    isComplete = questionnaire.isComplete,
    nextQuestions = questions.toJavaList()
  )
}

private def resolveQuestion(
  uid: Option[QuestionUid],
  questionUidToQuestionMap: Map[QuestionUid, QuestionEntity]
): IO[DomainError, Option[QuestionEntity]] = {
  if (uid.isDefined) {
    ZIO
      .fromOption(questionUidToQuestionMap.get(uid.get))
      .mapError(_ => DomainError(s"Failed to resolve question by uid: ${uid.get}"))
      .map(uid => Some(uid))
  } else {
    ZIO.succeed(None)
  }
}
