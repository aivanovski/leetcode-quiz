package com.github.ai.leetcodequiz.data.json

import com.fasterxml.jackson.core.{JsonFactory, JsonParser, JsonToken}
import com.github.ai.leetcodequiz.data.db.model.ProblemId
import com.github.ai.leetcodequiz.entity.{Difficulty, Problem}
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.stream.*

import java.io.InputStream

// The parser for leetcode problems stored in https://github.com/doocs/leetcode
@SuppressWarnings(
  Array("org.wartremover.warts.Throw")
)
class StreamingProblemParser {

  private val jsonFactory = JsonFactory()

  def parseStream(inputStream: InputStream): ZStream[Any, DomainError, Problem] =
    ZStream
      .scoped(
        ZIO.fromAutoCloseable(
          ZIO
            .attemptBlocking {
              val parser = jsonFactory.createParser(inputStream)

              if (parser.nextToken() != JsonToken.START_ARRAY) {
                parser.close()
                throw new IllegalArgumentException("Expected JSON array at root")
              }

              parser
            }
            .mapError(DomainError(_))
        )
      )
      .flatMap(parser =>
        ZStream.unfoldZIO(parser) { p =>
          ZIO
            .attemptBlocking {
              if (p.nextToken() == JsonToken.START_OBJECT) {
                parseProblemObject(p)
                  .map(problem => (problem, p))
              } else {
                None
              }
            }
            .mapError(DomainError(_))
        }
      )

  private def parseProblemObject(
    parser: JsonParser
  ): Option[Problem] = {
    var questionId: Option[String] = None
    var titleEn: Option[String] = None
    var contentEn: Option[String] = None
    var category: Option[String] = None
    var urlEn: Option[String] = None
    var difficultyEn: Option[String] = None

    while (parser.nextToken() != JsonToken.END_OBJECT) {
      val fieldName = parser.currentName()
      if (fieldName == null)
        return buildProblem(
          questionId = questionId,
          titleEn = titleEn,
          contentEn = contentEn,
          category = category,
          urlEn = urlEn,
          difficultyEn = difficultyEn
        )

      parser.nextToken()

      if (fieldName.endsWith("_cn")) {
        parser.skipChildren()
      } else {
        fieldName match {
          case "question_id" =>
            questionId = Option(parser.getValueAsString)
          case "title_en" =>
            titleEn = Option(parser.getValueAsString)
          case "content_en" =>
            contentEn = Option(parser.getValueAsString)
          case "category" =>
            category = Option(parser.getValueAsString)
          case "url_en" =>
            urlEn = Option(parser.getValueAsString)
          case "difficulty_en" =>
            difficultyEn = Option(parser.getValueAsString)
          case "code_snippets" | "tags_en" | "md_table_row_en" =>
            parser.skipChildren()
          case _ =>
            parser.skipChildren()
        }
      }
    }

    buildProblem(
      questionId,
      titleEn,
      contentEn,
      category,
      urlEn,
      difficultyEn
    )
  }

  private def buildProblem(
    questionId: Option[String],
    titleEn: Option[String],
    contentEn: Option[String],
    category: Option[String],
    urlEn: Option[String],
    difficultyEn: Option[String]
  ): Option[Problem] = {
    val idOption = questionId
      .flatMap(_.toLongOption)
      .map(ProblemId(_))
    if (idOption.isEmpty) {
      return None
    }

    val difficulty = difficultyEn
      .flatMap(Difficulty.from)
      .getOrElse(Difficulty.UNDEFINED)

    Some(
      Problem(
        id = idOption.get,
        title = titleEn.getOrElse(""),
        content = contentEn.getOrElse(""),
        category = category.getOrElse(""),
        url = urlEn.getOrElse(""),
        difficulty = difficulty,
        hints = List.empty,
        likes = 0,
        dislikes = 0
      )
    )
  }
}
