package com.github.ai.leetcodequiz.data.file

import com.github.ai.leetcodequiz.data.file.FileSystemProvider.PROPERTY_USER_DIR
import com.github.ai.leetcodequiz.data.file.FileSystemProvider.FILES_DIR_PATH
import com.github.ai.leetcodequiz.entity.{AbsolutePath, RelativePath}
import com.github.ai.leetcodequiz.entity.exception.{
  DomainError,
  NotADirectoryError,
  FileNotFoundError,
  FileSystemError
}
import zio.{IO, ZIO}
import zio.direct.{defer, run}

import java.io.{Reader, StringReader}
import java.nio.file.{Files, Path, Paths}
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

trait FileSystemProvider {
  def getDirPath(path: RelativePath): IO[FileSystemError, AbsolutePath]
  def remove(path: RelativePath): IO[FileSystemError, Unit]
  def readContent(path: RelativePath): IO[FileSystemError, String]
  def reader(path: RelativePath): IO[FileSystemError, Reader]
  def listFiles(path: RelativePath): IO[FileSystemError, List[RelativePath]]
  def listFileTree(path: RelativePath, maxDepth: Int): IO[FileSystemError, List[List[RelativePath]]]
}

class FileSystemProviderImpl(
  private val rootDirPath: Option[String] = None
) extends FileSystemProvider {

  override def getDirPath(
    path: RelativePath
  ): IO[FileSystemError, AbsolutePath] = defer {
    val root = getRootDirPath().run
    AbsolutePath(root.basePath, path.relativePath)
  }

  override def remove(
    path: RelativePath
  ): IO[FileSystemError, Unit] = defer {
    val file = convertToAbsolutePath(path).run

    ZIO
      .attempt {
        if (file.exists()) {
          if (file.isDirectory()) {
            // Remove directory recursively
            Files
              .walk(file.toPath())
              .sorted(java.util.Comparator.reverseOrder())
              .forEach(p => Files.delete(p))
          } else {
            // Remove file
            Files.delete(file.toPath())
          }
        }
      }
      .mapError(FileSystemError(_))
      .run
  }

  override def readContent(path: RelativePath): IO[FileSystemError, String] = defer {
    val absPath = convertToAbsolutePath(path).run

    ZIO
      .attempt {
        Files.readString(Paths.get(absPath.path))
      }
      .mapError(FileSystemError(_))
      .run
  }

  override def reader(path: RelativePath): IO[FileSystemError, Reader] = defer {
    val content = readContent(path).run
    StringReader(content)
  }

  override def listFiles(path: RelativePath): IO[FileSystemError, List[RelativePath]] = defer {
    val dir = convertToAbsolutePath(path).run

    if (!dir.exists()) {
      ZIO.fail(FileNotFoundError(dir)).run
    }
    if (!dir.isDirectory()) {
      ZIO.fail(NotADirectoryError(dir)).run
    }

    ZIO
      .attempt {
        val basePath = Paths.get(dir.basePath)

        Files
          .list(dir.toPath())
          .iterator()
          .asScala
          .map { filePath =>
            val relativePath = basePath.relativize(filePath).toString
            RelativePath(relativePath)
          }
          .toList
      }
      .mapError(FileSystemError(_))
      .run
  }

  override def listFileTree(
    path: RelativePath,
    maxDepth: Int
  ): IO[FileSystemError, List[List[RelativePath]]] = defer {
    val root = convertToAbsolutePath(path).run

    if (!root.exists()) {
      ZIO.fail(FileNotFoundError(root)).run
    }
    if (!root.isDirectory()) {
      ZIO.fail(NotADirectoryError(root)).run
    }

    val basePath = Paths.get(root.basePath)

    ZIO
      .attempt {
        val queue = ListBuffer[Path]()
        queue.addOne(root.toPath())

        val layers = ListBuffer[List[RelativePath]]()

        while (queue.nonEmpty && layers.size < maxDepth) {
          val layer = ListBuffer[RelativePath]()

          for (_ <- queue.indices) {
            val dir = queue.remove(0)

            val dirName = dir.getFileName.toString
            if (!dirName.startsWith(".")) {

              val children = Files.list(dir).iterator().asScala
              for (child <- children) {
                layer.addOne(
                  RelativePath(relativePath = basePath.relativize(child).toString)
                )

                if (Files.isDirectory(child)) {
                  queue.addOne(child)
                }
              }
            }
          }

          layers.addOne(layer.toList)
        }

        layers.toList
      }
      .mapError(e => FileSystemError(e))
      .run
  }

  private def convertToAbsolutePath(
    path: RelativePath
  ): IO[FileSystemError, AbsolutePath] = defer {
    val dir = getRootDirPath().run

    AbsolutePath(dir.path, path.path)
  }

  private def getRootDirPath(): IO[FileSystemError, AbsolutePath] = defer {
    val baseDirPath = if (rootDirPath.isEmpty) {
      val userDir = System.getProperty(PROPERTY_USER_DIR)

      if (userDir.isBlank) {
        ZIO
          .fail(
            FileSystemError(message = s"Failed to resolve environment variable: $PROPERTY_USER_DIR")
          )
          .run
      }

      userDir
    } else {
      rootDirPath.get
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
