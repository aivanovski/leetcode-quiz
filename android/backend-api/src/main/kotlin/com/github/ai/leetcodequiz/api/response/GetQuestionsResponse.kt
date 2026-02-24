package com.github.ai.leetcodequiz.api.response

import kotlinx.serialization.Serializable
import com.github.ai.leetcodequiz.api.QuestionItemDto

@Serializable
data class GetQuestionsResponse(
    val questions: List<QuestionItemDto>
)