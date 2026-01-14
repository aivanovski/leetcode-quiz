package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.entity.toRelativePath
import com.github.ai.leetcodequiz.entity.toFile
import com.github.ai.leetcodequiz.data.file.FileSystemProvider
import com.github.ai.leetcodequiz.entity.RelativePath
import com.github.ai.leetcodequiz.entity.exception.{DomainError, FileSystemError}
import org.eclipse.jgit.api.Git
import zio.*
import zio.direct.*

import java.util.UUID

class CloneGithubRepositoryUseCase(
  private val fileSystemProvider: FileSystemProvider
) {

  def cloneRepository(repositoryUrl: String): IO[DomainError, RelativePath] = defer {
    val destinationDirName = UUID.randomUUID().toString()
    val destination = fileSystemProvider.getDirPath(RelativePath(s"github/$destinationDirName")).run

    ZIO.logInfo(s"Cloning $repositoryUrl int ${destination.relativePath}").run

    ZIO
      .attempt {
        Git
          .cloneRepository()
          .setURI(repositoryUrl)
          .setDirectory(destination.toFile())
          .call()
      }
      .mapError(FileSystemError(_))
      .run

    destination.toRelativePath()
  }
}
