package com.github.ai.leetcodequiz.entity

import java.io.File

sealed class FilePath(
  path: String
)

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
