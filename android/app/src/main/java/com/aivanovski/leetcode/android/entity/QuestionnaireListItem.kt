package com.aivanovski.leetcode.android.entity

data class QuestionnaireListItem(
    val id: String,
    val isComplete: Boolean,
    val questionsIds: List<String>
)