package com.github.ai.leetcodequiz.api.response

import kotlinx.serialization.Serializable
import com.github.ai.leetcodequiz.api.QuestionnairesItemDto

@Serializable
data class GetQuestionnairesResponse(
    val questionnaires: List<QuestionnairesItemDto>
)