package com.github.ai.leetcodequiz.data.db

import com.github.ai.leetcodequiz.data.db.model.*
import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import slick.jdbc.SQLiteProfile.api.*
import zio.{IO, ZIO}
import zio.direct.*

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext

class AppDatabase(
  val context: Database
) {
  import SlickMappers.given
  private given ExecutionContext = context.executor.executionContext

  val DataSyncsTable = TableQuery[DataSyncEntityTable]
  val ProblemsTable = TableQuery[ProblemEntityTable]
  val ProblemHintsTable = TableQuery(new ProblemHintEntityTable(_, ProblemsTable))
  val QuestionsTable = TableQuery(new QuestionEntityTable(_, ProblemsTable))
  val QuestionnairesTable = TableQuery[QuestionnaireEntityTable]
  val AnswersTable = TableQuery(new AnswerEntityTable(_, QuestionnairesTable, QuestionsTable))
  val NextQuestionsTable = TableQuery(
    new NextQuestionEntityTable(_, QuestionnairesTable, QuestionsTable)
  )
  val UsersTable = TableQuery[UserEntityTable]
  val SolutionsTable = TableQuery(new SolutionEntityTable(_, ProblemsTable))

  private val enableForeignKeys = sqlu"PRAGMA foreign_keys = ON"

  def initialize(): IO[DatabaseError, Unit] = {
    run(
      for {
        _ <- enableForeignKeys
        _ <- DBIO.seq(
          DataSyncsTable.schema.createIfNotExists,
          ProblemsTable.schema.createIfNotExists,
          ProblemHintsTable.schema.createIfNotExists,
          QuestionsTable.schema.createIfNotExists,
          QuestionnairesTable.schema.createIfNotExists,
          AnswersTable.schema.createIfNotExists,
          NextQuestionsTable.schema.createIfNotExists,
          UsersTable.schema.createIfNotExists,
          SolutionsTable.schema.createIfNotExists
        )
      } yield ()
    )
  }

  def run[A](action: DBIO[A]): IO[DatabaseError, A] =
    ZIO.fromFuture(_ => context.run(action)).mapError(DatabaseError(_))
}

class DataSyncEntityTable(tag: Tag) extends Table[DataSyncEntity](tag, "data_syncs") {
  import SlickMappers.given

  val uid = column[SyncUid]("uid", O.PrimaryKey)
  val syncType = column[SyncType]("sync_type")
  val timestamp = column[LocalDateTime]("timestamp")
  val timestampValue = column[Long]("timestamp_value")

  override def * = (uid, syncType, timestamp, timestampValue).mapTo[DataSyncEntity]
}

class ProblemEntityTable(tag: Tag) extends Table[ProblemEntity](tag, "problems") {
  import SlickMappers.given

  val id = column[ProblemId]("id", O.PrimaryKey)
  val title = column[String]("title")
  val content = column[String]("content")
  val category = column[String]("category")
  val url = column[String]("url")
  val difficulty = column[String]("difficulty")
  val likes = column[Int]("likes")
  val dislikes = column[Int]("dislikes")

  override def * =
    (id, title, content, category, url, difficulty, likes, dislikes).mapTo[ProblemEntity]
}

class ProblemHintEntityTable(tag: Tag, problems: TableQuery[ProblemEntityTable])
    extends Table[ProblemHintEntity](tag, "problem_hints") {
  import SlickMappers.given

  val id = column[ProblemHintId]("id", O.PrimaryKey, O.AutoInc)
  val problemId = column[ProblemId]("problem_id")
  val hint = column[String]("hint")

  def problemFk =
    foreignKey("fk_problem_hints_problem_id", problemId, problems)(
      _.id,
      onDelete = ForeignKeyAction.Cascade
    )

  override def * = (id, problemId, hint).mapTo[ProblemHintEntity]
}

class QuestionEntityTable(tag: Tag, problems: TableQuery[ProblemEntityTable])
    extends Table[QuestionEntity](tag, "questions") {
  import SlickMappers.given

  val uid = column[QuestionUid]("uid", O.PrimaryKey)
  val problemId = column[ProblemId]("problem_id")
  val question = column[String]("question")
  val complexity = column[String]("complexity")

  def problemFk =
    foreignKey("fk_questions_problem_id", problemId, problems)(
      _.id,
      onDelete = ForeignKeyAction.Cascade
    )

  override def * = (uid, problemId, question, complexity).mapTo[QuestionEntity]
}

class QuestionnaireEntityTable(tag: Tag) extends Table[QuestionnaireEntity](tag, "questionnaires") {
  import SlickMappers.given

  val uid = column[QuestionnaireUid]("uid", O.PrimaryKey)
  val isComplete = column[Boolean]("is_complete")

  override def * = (uid, isComplete).mapTo[QuestionnaireEntity]
}

class AnswerEntityTable(
  tag: Tag,
  questionnaires: TableQuery[QuestionnaireEntityTable],
  questions: TableQuery[QuestionEntityTable]
) extends Table[AnswerEntity](tag, "answers") {
  import SlickMappers.given

  val uid = column[AnswerUid]("uid", O.PrimaryKey)
  val questionnaireUid = column[QuestionnaireUid]("questionnaire_uid")
  val questionUid = column[QuestionUid]("question_uid")
  val answer = column[Int]("answer")

  def questionnaireFk =
    foreignKey("fk_answers_questionnaire_uid", questionnaireUid, questionnaires)(
      _.uid,
      onDelete = ForeignKeyAction.Cascade
    )

  def questionFk =
    foreignKey("fk_answers_question_uid", questionUid, questions)(
      _.uid,
      onDelete = ForeignKeyAction.Cascade
    )

  override def * = (uid, questionnaireUid, questionUid, answer).mapTo[AnswerEntity]
}

class NextQuestionEntityTable(
  tag: Tag,
  questionnaires: TableQuery[QuestionnaireEntityTable],
  questions: TableQuery[QuestionEntityTable]
) extends Table[NextQuestionEntity](tag, "next_questions") {
  import SlickMappers.given

  val uid = column[NextQuestionUid]("uid", O.PrimaryKey)
  val questionnaireUid = column[QuestionnaireUid]("questionnaire_uid")
  val questionUid = column[QuestionUid]("question_uid")

  def questionnaireFk =
    foreignKey("fk_next_questions_questionnaire_uid", questionnaireUid, questionnaires)(
      _.uid,
      onDelete = ForeignKeyAction.Cascade
    )

  def questionFk =
    foreignKey("fk_next_questions_question_uid", questionUid, questions)(
      _.uid,
      onDelete = ForeignKeyAction.Cascade
    )

  override def * = (uid, questionnaireUid, questionUid).mapTo[NextQuestionEntity]
}

class UserEntityTable(tag: Tag) extends Table[UserEntity](tag, "users") {
  import SlickMappers.given

  val uid = column[UserUid]("uid", O.PrimaryKey)
  val name = column[String]("name")
  val email = column[String]("email")
  val passwordHash = column[String]("password_hash")

  override def * = (uid, name, email, passwordHash).mapTo[UserEntity]
}

class SolutionEntityTable(tag: Tag, problems: TableQuery[ProblemEntityTable])
    extends Table[SolutionEntity](tag, "solutions") {
  import SlickMappers.given

  val uid = column[SolutionUid]("uid", O.PrimaryKey)
  val problemId = column[ProblemId]("problem_id")
  val path = column[String]("path")
  val content = column[String]("content")

  def problemFk =
    foreignKey("fk_solutions_problem_id", problemId, problems)(
      _.id,
      onDelete = ForeignKeyAction.Cascade
    )

  override def * = (uid, problemId, path, content).mapTo[SolutionEntity]
}
