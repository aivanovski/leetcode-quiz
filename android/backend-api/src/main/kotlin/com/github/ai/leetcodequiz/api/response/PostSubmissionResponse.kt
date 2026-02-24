package com.github.ai.leetcodequiz.api.response

import kotlinx.serialization.Serializable
import com.github.ai.leetcodequiz.api.QuestionnaireItemDto

@Serializable
data class PostSubmissionResponse(
    val questionnaire: QuestionnaireItemDto
)