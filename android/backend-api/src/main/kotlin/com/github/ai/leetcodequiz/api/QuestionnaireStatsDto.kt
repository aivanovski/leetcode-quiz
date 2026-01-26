package com.github.ai.leetcodequiz.api

import kotlinx.serialization.Serializable

@Serializable
data class QuestionnaireStatsDto(
    val totalQuestions: Int,
    val answered: Int,
    val notAnswered: Int,
    val answeredPositively: Int,
    val answeredNegatively: Int
)