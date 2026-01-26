package com.github.ai.leetcodequiz.api

import kotlinx.serialization.Serializable

@Serializable
data class QuestionnaireItemDto(
    val id: String,
    val isComplete: Boolean,
    val nextQuestions: List<QuestionItemDto>,
    val problems: List<ProblemItemDto>,
    val stats: QuestionnaireStatsDto
)