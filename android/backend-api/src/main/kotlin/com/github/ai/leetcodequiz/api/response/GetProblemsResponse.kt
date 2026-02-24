package com.github.ai.leetcodequiz.api.response

import kotlinx.serialization.Serializable
import com.github.ai.leetcodequiz.api.ProblemsItemDto

@Serializable
data class GetProblemsResponse(
    val problems: List<ProblemsItemDto>
)