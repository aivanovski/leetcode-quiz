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
  solutions: List[SolutionItemDto],
  questions: List[QuestionItemDto],
  likes: Int,
  dislikes: Int
) derives JsonEncoder,
      JsonDecoder
