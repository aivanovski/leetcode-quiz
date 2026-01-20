package com.github.ai.leetcodequiz.api

import zio.json.{JsonDecoder, JsonEncoder}

case class ProblemItemDto(
  id: Int,
  title: String,
  content: String,
  hints: List[String],
  categoryTitle: String,
  difficulty: String,
  url: String,
  likes: Int,
  dislikes: Int
) derives JsonEncoder, JsonDecoder
