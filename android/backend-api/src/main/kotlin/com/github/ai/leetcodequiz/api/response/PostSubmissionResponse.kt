package com.github.ai.leetcodequiz.api.response

import kotlinx.serialization.Serializable
import com.github.ai.leetcodequiz.api.QuestionnairesItemDto

@Serializable
data class PostSubmissionResponse(
    val questionnaire: QuestionnairesItemDto
)