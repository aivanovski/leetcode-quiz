package com.github.ai.leetcodequiz.entity

import java.io.File

sealed class FilePath(
  val path: String
) {

  def getName(): String = {
    val lastSeparatorIdx = path.lastIndexOf("/")

    if (lastSeparatorIdx < 0) return path
    if (lastSeparatorIdx == path.length - 1) return ""

    path.substring(lastSeparatorIdx + 1, path.length)
  }
}

case class AbsolutePath(
  basePath: String,
  relativePath: String
) extends FilePath(path = basePath.stripSuffix("/") + "/" + relativePath.stripPrefix("/"))

case class RelativePath(
  relativePath: String
) extends FilePath(path = relativePath)

extension (file: AbsolutePath) {
  def toRelativePath(): RelativePath = RelativePath(file.relativePath)

  def toFile(): File = File(file.basePath + "/" + file.relativePath)
}
