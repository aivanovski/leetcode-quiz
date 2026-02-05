package com.github.ai.leetcodequiz.api

import kotlinx.serialization.Serializable

@Serializable
data class QuestionnaireItemDto(
    val id: String,
    val isComplete: Boolean,
    val questions: List<QuestionItemDto>,
    val answers: List<QuestionAnswerDto>,
    val stats: QuestionnaireStatsDto
)