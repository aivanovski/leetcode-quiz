package com.aivanovski.leetcode.android.entity

import com.github.ai.leetcodequiz.api.QuestionnaireStatsDto

data class Questionnaire(
    val id: String,
    val isComplete: Boolean,
    val questions: List<Question>,
    val stats: QuestionnaireStatsDto
)