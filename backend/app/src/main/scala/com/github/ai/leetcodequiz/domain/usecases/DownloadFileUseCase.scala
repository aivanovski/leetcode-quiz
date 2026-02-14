package com.github.ai.leetcodequiz.domain.usecases

import com.github.ai.leetcodequiz.data.file.FileSystemProvider
import com.github.ai.leetcodequiz.entity.{AbsolutePath, RelativePath}
import com.github.ai.leetcodequiz.entity.exception.{DomainError, FileSystemError}
import zio.*
import zio.direct.*
import zio.http.*
import zio.stream.ZSink

import java.io.FileOutputStream

class DownloadFileUseCase(
  private val fileSystemProvider: FileSystemProvider,
  private val client: Client
) {

  def downloadFile(
    url: String,
    destinationDir: RelativePath,
    destinationFile: RelativePath
  ): IO[DomainError, AbsolutePath] = defer {
    val absDestinationDir = fileSystemProvider.ensureDir(destinationDir).run

    val absFile = fileSystemProvider.resolveFile(destinationFile).run

    ZIO.logInfo(s"Downloading file $url into: ${destinationFile.relativePath}").run

    ZIO
      .scoped {
        client
          .request(Request.get(url))
          .flatMap { response =>
            if (response.status.code >= 200 && response.status.code < 300) {
              ZIO
                .fromAutoCloseable(
                  ZIO.attempt(new FileOutputStream(absFile.toFile()))
                )
                .flatMap { outputStream =>
                  response.body.asStream
                    .run(ZSink.fromOutputStream(outputStream))
                    .as(absFile)
                }
            } else {
              ZIO.fail(
                DomainError(s"Download failed: HTTP ${response.status.code} for $url")
              )
            }
          }
      }
      .mapError {
        case e: DomainError => e
        case t: Throwable => FileSystemError(t)
      }
      .run
  }

}
