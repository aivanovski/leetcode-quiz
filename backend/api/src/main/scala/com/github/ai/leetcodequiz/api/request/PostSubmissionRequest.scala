package com.github.ai.leetcodequiz.api.request

import zio.json.{JsonDecoder, JsonEncoder}

case class PostSubmissionRequest(
  questionId: String,
  answer: Int
) derives JsonEncoder,
      JsonDecoder
