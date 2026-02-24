package com.github.ai.leetcodequiz.api

import kotlinx.serialization.Serializable

@Serializable
data class QuestionItemDto(
    val id: String,
    val problemId: Int,
    val question: String,
    val complexity: String
)