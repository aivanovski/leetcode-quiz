package com.github.ai.leetcodequiz.api

import kotlinx.serialization.Serializable

@Serializable
data class QuestionnairesItemDto(
    val id: String,
    val isComplete: Boolean,
    val nextQuestions: List<QuestionItemDto>,
    val stats: QuestionnaireStatsDto
)