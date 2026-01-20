package com.github.ai.leetcodequiz.api

import zio.json.{JsonDecoder, JsonEncoder}

case class ProblemsItemDto(
  id: Int,
  title: String,
  categoryTitle: String,
  difficulty: String,
  url: String,
  dislikes: Int,
  likes: Int
) derives JsonEncoder, JsonDecoder
