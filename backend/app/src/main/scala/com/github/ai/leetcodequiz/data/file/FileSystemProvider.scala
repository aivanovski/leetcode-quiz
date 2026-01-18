package com.github.ai.leetcodequiz.data.file

import com.github.ai.leetcodequiz.data.file.FileSystemProvider.PROPERTY_USER_DIR
import com.github.ai.leetcodequiz.data.file.FileSystemProvider.FILES_DIR_PATH
import com.github.ai.leetcodequiz.entity.{AbsolutePath, RelativePath}
import com.github.ai.leetcodequiz.entity.exception.{DomainError, FileSystemError}
import zio.{IO, ZIO}
import zio.direct.{defer, run}

import java.io.{Reader, StringReader}
import java.nio.file.{Files, Path, Paths}
import scala.jdk.CollectionConverters.*

trait FileSystemProvider {
  def getDirPath(path: RelativePath): IO[DomainError, AbsolutePath]
  def remove(path: RelativePath): IO[DomainError, Unit]
  def readContent(path: RelativePath): IO[DomainError, String]
  def reader(path: RelativePath): IO[DomainError, Reader]
  def listFiles(path: RelativePath): IO[DomainError, List[RelativePath]]
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
    val absPath = convertToAbsolutePath(path).run
    val filePath = Paths.get(absPath.path)

    ZIO
      .attempt {
        if (Files.exists(filePath)) {
          if (Files.isDirectory(filePath)) {
            // Remove directory recursively
            Files
              .walk(filePath)
              .sorted(java.util.Comparator.reverseOrder())
              .forEach(p => Files.delete(p))
          } else {
            // Remove file
            Files.delete(filePath)
          }
        }
      }
      .mapError(FileSystemError(_))
      .run
  }

  override def readContent(path: RelativePath): IO[DomainError, String] = defer {
    val absPath = convertToAbsolutePath(path).run

    ZIO
      .attempt {
        Files.readString(Paths.get(absPath.path))
      }
      .mapError(FileSystemError(_))
      .run
  }

  override def reader(path: RelativePath): IO[DomainError, Reader] = defer {
    val content = readContent(path).run
    StringReader(content)
  }

  override def listFiles(path: RelativePath): IO[DomainError, List[RelativePath]] = defer {
    val absPath = convertToAbsolutePath(path).run
    val dirPath = Paths.get(absPath.path)

    ZIO
      .attempt {
        if (!Files.exists(dirPath)) {
          List.empty[RelativePath]
        } else if (!Files.isDirectory(dirPath)) {
          List.empty[RelativePath]
        } else {
          val rootPath = Paths.get(absPath.basePath)

          Files
            .list(dirPath)
            .iterator()
            .asScala
            .map { filePath =>
              val relativePath = rootPath.relativize(filePath).toString
              RelativePath(relativePath)
            }
            .toList
        }
      }
      .mapError(FileSystemError(_))
      .run
  }

  private def convertToAbsolutePath(
    path: RelativePath
  ): IO[DomainError, AbsolutePath] = defer {
    val dir = getRootDirPath().run

    AbsolutePath(dir.path, path.path)
  }

  private def getRootDirPath(): IO[DomainError, AbsolutePath] = defer {
    val baseDirPath = System.getProperty(PROPERTY_USER_DIR)

    if (baseDirPath.isBlank) {
      ZIO
        .fail(
          FileSystemError(message = s"Failed to resolve environment variable: $PROPERTY_USER_DIR")
        )
        .run
    }

    val fileDirPath = Paths.get(s"$baseDirPath/$FILES_DIR_PATH")
    if (!Files.exists(fileDirPath)) {
      ZIO
        .attempt {
          Files.createDirectories(fileDirPath)
        }
        .mapError(FileSystemError(_))
        .run
    }

    AbsolutePath(fileDirPath.toString, "/")
  }
}

object FileSystemProvider {
  val PROPERTY_USER_DIR = "user.dir"
  val FILES_DIR_PATH = "app-data/files"
}
