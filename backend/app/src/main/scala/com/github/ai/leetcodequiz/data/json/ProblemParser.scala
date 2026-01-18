package com.github.ai.leetcodequiz.data.json

import com.github.ai.leetcodequiz.data.db.model.ProblemId
import com.github.ai.leetcodequiz.entity.{Difficulty, Problem}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import com.github.ai.leetcodequiz.utils.parseJson
import zio.*
import zio.direct.*
import zio.json.*

class ProblemParser {

  def parse(jsonContent: String): IO[DomainError, List[Problem]] = defer {
    val data = jsonContent.parseJson[List[DataItem]].run

    val problems = ZIO.collectAll {
      data.map { item => convertProblem(problem = item.data.question) }
    }.run

    problems
  }

  private def convertProblem(
    problem: ProblemJsonEntity
  ): IO[DomainError, Problem] = defer {
    val id = ZIO.attempt(Integer.parseInt(problem.questionId)).mapError(DomainError(_)).run

    val difficulty = Difficulty
      .from(problem.difficulty)
      .getOrElse(Difficulty.UNDEFINED)

    Problem(
      id = ProblemId(id),
      title = problem.title,
      content = problem.content.getOrElse(""),
      category = problem.categoryTitle,
      url = problem.url,
      difficulty = difficulty,
      hints = problem.hints,
      likes = problem.likes,
      dislikes = problem.dislikes
    )
  }

  case class DataItem(
    data: ProblemItem
  )

  case class ProblemItem(
    question: ProblemJsonEntity
  )

  case class ProblemJsonEntity(
    questionId: String,
    title: String,
    content: Option[String],
    categoryTitle: String,
    url: String,
    difficulty: String,
    hints: List[String],
    likes: Int,
    dislikes: Int
  )

  object DataItem {
    implicit val decoder: JsonDecoder[DataItem] =
      DeriveJsonDecoder.gen[DataItem]
  }

  object ProblemItem {
    implicit val decoder: JsonDecoder[ProblemItem] =
      DeriveJsonDecoder.gen[ProblemItem]
  }

  object ProblemJsonEntity {
    implicit val decoder: JsonDecoder[ProblemJsonEntity] =
      DeriveJsonDecoder.gen[ProblemJsonEntity]
  }
}
