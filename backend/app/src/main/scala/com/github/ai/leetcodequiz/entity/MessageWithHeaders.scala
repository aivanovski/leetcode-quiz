package com.github.ai.leetcodequiz.entity

import scalapb.GeneratedMessage
import zio.http.Header

case class MessageWithHeaders[T <: GeneratedMessage](
  headers: List[Header],
  message: T
)
