package com.github.ai.leetcodequiz.utils

import com.github.ai.leetcodequiz.api.{
  ProblemItemDto,
  ProblemsItemDto,
  QuestionAnswerDto,
  QuestionItemDto,
  QuestionnaireItemDto,
  QuestionnaireStatsDto,
  QuestionnairesItemDto,
  UserDto as UserDtoSc
}
import com.github.ai.leetcodequiz.data.db.model.{
  ProblemId,
  QuestionEntity,
  QuestionUid,
  QuestionnaireEntity,
  UserEntity
}
import com.github.ai.leetcodequiz.entity.{Problem, Questionnaire, QuestionnaireStats}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

def toProblemItemDto(problem: Problem) =
  ProblemItemDto(
    id = problem.id.toString.toInt,
    title = problem.title,
    content = problem.content,
    hints = problem.hints,
    categoryTitle = problem.category,
    difficulty = problem.difficulty.toString,
    url = problem.url,
    likes = problem.likes,
    dislikes = problem.dislikes
  )

def toProblemsItemDto(problem: Problem) =
  ProblemsItemDto(
    id = problem.id.toString.toInt,
    title = problem.title,
    categoryTitle = problem.category,
    difficulty = problem.difficulty.toString,
    url = problem.url,
    likes = problem.likes,
    dislikes = problem.dislikes
  )

def toUserDto(user: UserEntity) =
  UserDtoSc(
    name = user.name,
    email = user.email
  )

def toQuestionnaireStatsDto(stats: QuestionnaireStats) =
  QuestionnaireStatsDto(
    totalQuestions = stats.answeredQuestions + stats.notAnsweredQuestions,
    answered = stats.answeredQuestions,
    notAnswered = stats.notAnsweredQuestions,
    answeredPositively = stats.answeredPositively,
    answeredNegatively = stats.answeredNegatively
  )

def toQuestionItemDtos(questions: List[QuestionEntity]) =
  questions.map { question => toQuestionItemDto(question) }

def toQuestionItemDto(question: QuestionEntity) =
  QuestionItemDto(
    id = question.uid.toString,
    problemId = question.problemId.toString.toInt,
    question = question.question,
    complexity = question.complexity
  )

def toQuestionnaireItemDto(
  questionnaire: Questionnaire,
  stats: QuestionnaireStats,
  questionUidToQuestionMap: Map[QuestionUid, QuestionEntity]
): IO[DomainError, QuestionnaireItemDto] = defer {
  val questions = resolveQuestions(
    uids = questionnaire.questions,
    questionUidToQuestionMap = questionUidToQuestionMap
  ).run

  val questionDtos = toQuestionItemDtos(questions)

  QuestionnaireItemDto(
    id = questionnaire.uid.toString,
    isComplete = questionnaire.isComplete,
    questions = questionDtos,
    answers = questionnaire.answers.map { answer =>
      QuestionAnswerDto(answer.uid.toString, answer.answer)
    },
    stats = toQuestionnaireStatsDto(stats)
  )
}

def toQuestionnairesItemDto(
  questionnaire: Questionnaire
): IO[DomainError, QuestionnairesItemDto] = defer {
  QuestionnairesItemDto(
    id = questionnaire.uid.toString,
    isComplete = questionnaire.isComplete,
    questions = questionnaire.questions.map(_.toString)
  )
}

private def resolveQuestions(
  uids: List[QuestionUid],
  questionUidToQuestionMap: Map[QuestionUid, QuestionEntity]
): IO[DomainError, List[QuestionEntity]] = {
  ZIO
    .collectAll(
      uids.map { uid =>
        resolveQuestion(uid, questionUidToQuestionMap)
      }
    )
}

private def resolveQuestion(
  uid: QuestionUid,
  questionUidToQuestionMap: Map[QuestionUid, QuestionEntity]
): IO[DomainError, QuestionEntity] = {
  ZIO
    .fromOption(questionUidToQuestionMap.get(uid))
    .mapError(_ => DomainError(s"Failed to resolve question by uid: $uid"))
}

private def resolveProblem(
  id: ProblemId,
  problemIdToProblemMap: Map[ProblemId, Problem]
): IO[DomainError, Problem] = {
  ZIO
    .fromOption(problemIdToProblemMap.get(id))
    .mapError(_ => DomainError(s"Failed to resolve problemy by id: $id"))
}
