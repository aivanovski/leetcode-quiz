package com.github.ai.leetcodequiz.api.response

import kotlinx.serialization.Serializable
import com.github.ai.leetcodequiz.api.QuestionnaireItemDto
import com.github.ai.leetcodequiz.api.QuestionnairesItemDto

@Serializable
data class GetQuestionnaireResponse(
    val questionnaire: QuestionnaireItemDto
)