package com.aivanovski.leetcode.android.entity

data class ProblemWithContent(
    val problem: Problem,
    val content: String,
    val hints: List<String>,
    val solutions: List<String>,
    val questions: List<Question>
)