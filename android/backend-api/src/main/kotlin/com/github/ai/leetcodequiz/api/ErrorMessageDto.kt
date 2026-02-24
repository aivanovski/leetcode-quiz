package com.github.ai.leetcodequiz.api

import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessageDto(
    val message: String,
    val exception: String,
    val stacktraceBase64: String,
    val stacktraceLines: List<String>
)