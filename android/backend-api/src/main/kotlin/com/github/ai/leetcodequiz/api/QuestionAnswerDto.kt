package com.github.ai.leetcodequiz.api

import kotlinx.serialization.Serializable

@Serializable
data class QuestionAnswerDto(
    val id: String,
    val answer: Int
)