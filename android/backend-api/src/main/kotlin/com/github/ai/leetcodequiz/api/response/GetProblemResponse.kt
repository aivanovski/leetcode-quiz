package com.github.ai.leetcodequiz.api.response

import kotlinx.serialization.Serializable
import com.github.ai.leetcodequiz.api.ProblemItemDto

@Serializable
data class GetProblemResponse(
    val problem: ProblemItemDto
)