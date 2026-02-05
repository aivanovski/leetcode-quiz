package com.github.ai.leetcodequiz.api

import kotlinx.serialization.Serializable

@Serializable
data class SolutionItemDto(
    val contentBase64: String
)