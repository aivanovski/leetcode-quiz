package com.github.ai.leetcodequiz.api

import kotlinx.serialization.Serializable

@Serializable
data class ProblemItemDto(
    val id: Int,
    val title: String,
    val content: String,
    val hints: List<String>,
    val categoryTitle: String,
    val difficulty: String,
    val url: String,
    val likes: Int,
    val dislikes: Int
)