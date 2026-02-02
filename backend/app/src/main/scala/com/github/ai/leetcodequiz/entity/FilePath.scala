package com.github.ai.leetcodequiz.entity

import java.io.File
import java.nio.file.{Files, LinkOption, Path, Paths}

sealed class FilePath(
  val path: String
) {

  def getName(): String = {
    val lastSeparatorIdx = path.lastIndexOf("/")

    if (lastSeparatorIdx < 0) return path
    if (lastSeparatorIdx == path.length - 1) return ""

    path.substring(lastSeparatorIdx + 1, path.length)
  }

  override def toString: String = path
}

case class AbsolutePath(
  basePath: String,
  relativePath: String
) extends FilePath(path = basePath.stripSuffix("/") + "/" + relativePath.stripPrefix("/")) {

  def toRelativePath(): RelativePath = RelativePath(relativePath)
  def toFile(): File = File(path)
  def toPath(): Path = Paths.get(path)
  def isDirectory(): Boolean = Files.isDirectory(toPath())
  def exists(): Boolean = Files.exists(toPath(), LinkOption.NOFOLLOW_LINKS)
}

case class RelativePath(
  relativePath: String
) extends FilePath(path = relativePath) {

  def relativize(child: RelativePath): RelativePath = {
    val path = child.relativePath.stripPrefix(this.relativePath)
    RelativePath(path)
  }
}
