package com.github.ai.leetcodequiz.data.file

import com.github.ai.leetcodequiz.data.file.FileSystemProvider.PROPERTY_USER_DIR
import com.github.ai.leetcodequiz.data.file.FileSystemProvider.FILES_DIR_PATH
import com.github.ai.leetcodequiz.entity.{AbsolutePath, RelativePath}
import com.github.ai.leetcodequiz.entity.exception.{DomainError, FileSystemError}
import zio.{IO, ZIO}
import zio.direct.{defer, run}

import java.nio.file.{Files, Paths}

trait FileSystemProvider {
  def getDirPath(path: RelativePath): IO[DomainError, AbsolutePath]
  def remove(path: RelativePath): IO[DomainError, Unit]
}

class FileSystemProviderImpl extends FileSystemProvider {

  override def getDirPath(
    path: RelativePath
  ): IO[DomainError, AbsolutePath] = defer {
    val root = getRootDirPath().run
    AbsolutePath(root.basePath, path.relativePath)
  }

  override def remove(
    path: RelativePath
  ): IO[DomainError, Unit] = defer {
    ()
  }

  private def getRootDirPath(): IO[DomainError, AbsolutePath] = defer {
    val baseDirPath = System.getProperty(PROPERTY_USER_DIR)

    ZIO.logInfo(s"calling").run

    if (baseDirPath.isBlank) {
      ZIO.fail(FileSystemError(message = s"Failed to resolve environment variable: $PROPERTY_USER_DIR")).run
    }

    val path = Paths.get(s"$baseDirPath/$FILES_DIR_PATH")
    if (!Files.exists(path)) {
      ZIO
        .attempt {
          Files.createDirectories(path)
        }
        .mapError(FileSystemError(_))
        .run
    }

    AbsolutePath(path.toString, "/")
  }
}

object FileSystemProvider {
  val PROPERTY_USER_DIR = "user.dir"
  val FILES_DIR_PATH = "app-data/files"
}
