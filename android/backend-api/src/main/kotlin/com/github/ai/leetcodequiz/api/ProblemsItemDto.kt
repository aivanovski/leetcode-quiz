package com.github.ai.leetcodequiz.api

import kotlinx.serialization.Serializable

@Serializable
data class ProblemsItemDto(
    val id: Int,
    val title: String,
    val categoryTitle: String,
    val difficulty: String,
    val url: String,
    val dislikes: Int,
    val likes: Int
)