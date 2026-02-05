package com.github.ai.leetcodequiz.utils

import java.util.Properties
import scala.jdk.CollectionConverters.*

extension [T](list: List[T]) {
  def toJavaList(): java.util.List[T] = list.asJava
}

extension [T](javaList: java.util.List[T]) {
  def toScalaList(): List[T] = javaList.asScala.toList
}

extension (values: Map[String, String]) {
  def toProperties(): Properties = {
    val properties = Properties()

    values.foreachEntry { (key, value) =>
      properties.put(key, value)
    }

    properties
  }
}
