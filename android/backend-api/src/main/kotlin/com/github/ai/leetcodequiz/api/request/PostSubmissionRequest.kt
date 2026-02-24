package com.github.ai.leetcodequiz.api.request

import kotlinx.serialization.Serializable

@Serializable
data class PostSubmissionRequest(
    val questionId: String,
    val answer: Int
)