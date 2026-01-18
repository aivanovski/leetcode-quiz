package com.github.ai.leetcodequiz.domain.jobs

import com.github.ai.leetcodequiz.data.db.model.{
  DataSyncEntity,
  ProblemId,
  QuestionEntity,
  QuestionUid,
  SyncType,
  SyncUid
}
import com.github.ai.leetcodequiz.data.db.repository.{
  DataSyncRepository,
  ProblemRepository,
  QuestionRepository
}
import com.github.ai.leetcodequiz.data.file.FileSystemProvider
import com.github.ai.leetcodequiz.domain.usecases.CloneGithubRepositoryUseCase
import com.github.ai.leetcodequiz.entity.RelativePath
import com.github.ai.leetcodequiz.entity.exception.{DomainError, ParsingError}
import org.apache.commons.csv.{CSVFormat, CSVRecord}
import zio.*
import zio.direct.*

import java.io.StringReader
import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID
import scala.collection.JavaConverters.asScalaIteratorConverter

class SyncQuestionsJob(
  private val cloneRepositoryUseCase: CloneGithubRepositoryUseCase,
  private val syncRepository: DataSyncRepository,
  private val problemRepository: ProblemRepository,
  private val questionRepository: QuestionRepository,
  private val fileSystemProvider: FileSystemProvider
) extends ScheduledJob {

  override val interval: Duration = 12.hours

  override def run(): IO[DomainError, Unit] = defer {
    ZIO.logInfo("Start sync questions job.").run

    val syncUid = UUID.randomUUID()
    val lastSync = syncRepository.getLatestSync(SyncType.QUESTIONS).run
    val destinationDirPath = RelativePath(s"github/${syncUid.toString}")

    val timeThreshold = LocalDateTime.now(ZoneOffset.UTC).minusHours(2)
    val shouldSync = lastSync.isEmpty || lastSync.get.timestamp.isBefore(timeThreshold)

    ZIO
      .logInfo(
        "Last questions sync happened: %s; last sync time: %s, should do sync: %s".format(
          "-",
          lastSync.map(_.timestamp),
          shouldSync
        )
      )
      .run

    if (shouldSync) {
      val repoDir = cloneRepositoryUseCase
        .cloneRepository(
          repositoryUrl = "https://github.com/aivanovski/leetcode-notes.git",
          destinationDirPath = destinationDirPath
        )
        .run

      val solutionsFile = RelativePath(s"${repoDir.path}/Leetcode challenges - Blind 75.csv")

      val remoteQuestions = parseContent(solutionsFile).run
      val localQuestions = questionRepository.getAll().run

      syncQuestionsWithDatabase(
        remoteQuestions = remoteQuestions,
        localQuestions = localQuestions
      ).run

      syncRepository
        .add(
          DataSyncEntity(
            uid = SyncUid(syncUid),
            syncType = SyncType.QUESTIONS,
            timestamp = LocalDateTime.now(ZoneOffset.UTC)
          )
        )
        .run

      ZIO.logInfo(s"Removing files in: ${destinationDirPath.relativePath}").run
      fileSystemProvider.remove(destinationDirPath).run
    }

    ZIO.logInfo("Sync questions job finished.").run
    ()
  }

  private def syncQuestionsWithDatabase(
    remoteQuestions: List[QuestionItem],
    localQuestions: List[QuestionEntity]
  ): IO[DomainError, Unit] = defer {
    val remoteQuestionsMap = remoteQuestions.map(question => (question.id, question)).toMap
    val localQuestionsMap =
      localQuestions.map(question => (question.problemId.asInstanceOf[Long], question)).toMap

    val remoteIds = remoteQuestionsMap.keySet
    val localIds = localQuestionsMap.keySet

    val insertions = remoteIds.diff(localIds).toList.sorted
    val deletions = localIds.diff(remoteIds).toList.sorted
    val potentialUpdates = remoteIds.intersect(localIds).toList.sorted

    val updates = potentialUpdates.filter { id =>
      val remote = remoteQuestionsMap(id)
      val local = localQuestionsMap(id)

      local.complexity != remote.complexity || local.question != remote.solution
    }

    ZIO
      .logInfo(
        s"Question sync summary: ${insertions.size} insertions, ${updates.size} updates, ${deletions.size} deletions"
      )
      .run

    if (insertions.nonEmpty) {
      ZIO.logInfo(s"Inserting ${insertions.size} new questions:").run

      for (id <- insertions) {
        val remote = remoteQuestionsMap(id)
        val local = toDatbaseEntity(remote)
        ZIO.logInfo(s"  + [$id] ${remote.name}").run
        questionRepository.add(local).run
      }
    }

    if (updates.nonEmpty) {
      ZIO.logInfo(s"Updating ${updates.size} questions:").run

      for (id <- updates) {
        val remote = remoteQuestionsMap(id)
        val local = localQuestionsMap(id)
        val updated = local.copy(
          question = remote.solution,
          complexity = remote.complexity
        )
        ZIO.logInfo(s"  ~ [$id] ${remote.name}").run
        questionRepository.update(updated).run
      }
    }

    if (deletions.nonEmpty) {
      ZIO.logInfo(s"Deleting ${deletions.size} questions:").run

      for (id <- deletions) {
        val local = localQuestionsMap(id)
        ZIO.logInfo(s"  - [$id]").run
        questionRepository.delete(local.uid).run
      }
    }

    if (insertions.isEmpty && updates.isEmpty && deletions.isEmpty) {
      ZIO.logInfo("No question changes detected. Database is up to date.").run
    }

    ()
  }

  private def parseContent(
    csvFile: RelativePath
  ): IO[DomainError, List[QuestionItem]] = {
    for {
      records <- readCsvFile(csvFile)
      items <- ZIO
        .collectAll {
          records
            .map { record =>
              val itemOption = parseQuestionItem(record)
              if (itemOption.isEmpty) {
                ZIO
                  .logError(s"Failed to parse csv record: $record")
                  .map(_ => None)
              } else {
                ZIO.succeed(itemOption)
              }
            }
        }
    } yield items.flatten
  }

  private def readCsvFile(
    csvFile: RelativePath
  ): IO[DomainError, List[CSVRecord]] = defer {
    val content = fileSystemProvider.readContent(csvFile).run

    ZIO
      .acquireReleaseWith(ZIO.attemptBlocking(StringReader(content)))(reader =>
        ZIO.attempt(reader.close()).orDie
      ) { reader =>
        ZIO.attemptBlocking {
          val format = CSVFormat.DEFAULT
            .builder()
            .setHeader(
              CsvColumns.Name,
              CsvColumns.Solved,
              CsvColumns.Solution,
              CsvColumns.Complexity
            )
            .setSkipHeaderRecord(true)
            .build()

          format
            .parse(reader)
            .iterator()
            .asScala
            .toList
        }
      }
      .mapError(ParsingError(_))
      .run
  }

  private def parseQuestionItem(
    record: CSVRecord
  ): Option[QuestionItem] = {
    val idAndName = record.get(CsvColumns.Name)
    val solved = record.get(CsvColumns.Solved)
    val solution = record.get(CsvColumns.Solution)
    val complexity = record.get(CsvColumns.Complexity)

    val idOption = idAndName
      .split("\\.")
      .headOption
      .flatMap(_.toLongOption)
    if (idOption.isEmpty) return None

    val nameOption = idAndName.split("\\.").lastOption
    if (nameOption.isEmpty) return None

    Some(
      QuestionItem(
        id = idOption.getOrElse(-1),
        name = nameOption.getOrElse(""),
        solved = solved,
        solution = solution,
        complexity = complexity
      )
    )
  }

  private def toDatbaseEntity(question: QuestionItem) =
    QuestionEntity(
      uid = QuestionUid(UUID.randomUUID()),
      problemId = ProblemId(question.id),
      question = question.solution,
      complexity = question.complexity
    )

  private case class QuestionItem(
    id: Long,
    name: String,
    solved: String,
    solution: String,
    complexity: String
  )

  private object CsvColumns {
    val Name = "Name"
    val Solved = "Solved"
    val Solution = "Solution"
    val Complexity = "Complexity"
  }
}
