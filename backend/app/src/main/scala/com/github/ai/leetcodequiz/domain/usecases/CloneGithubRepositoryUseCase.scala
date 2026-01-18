package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.entity.toRelativePath
import com.github.ai.leetcodequiz.entity.toFile
import com.github.ai.leetcodequiz.data.file.FileSystemProvider
import com.github.ai.leetcodequiz.entity.RelativePath
import com.github.ai.leetcodequiz.entity.exception.{DomainError, FileSystemError}
import org.eclipse.jgit.api.Git
import zio.*
import zio.direct.*

class CloneGithubRepositoryUseCase(
  private val fileSystemProvider: FileSystemProvider
) {

  def cloneRepository(
    repositoryUrl: String,
    destinationDirPath: RelativePath
  ): IO[DomainError, RelativePath] = defer {
    val destination = fileSystemProvider.getDirPath(destinationDirPath).run

    ZIO.logInfo(s"Cloning $repositoryUrl into: ${destination.relativePath}").run

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
