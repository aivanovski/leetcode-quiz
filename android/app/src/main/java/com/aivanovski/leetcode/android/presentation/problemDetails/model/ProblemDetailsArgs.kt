package com.aivanovski.leetcode.android.presentation.problemDetails.model

import androidx.compose.runtime.Immutable

@Immutable
data class ProblemDetailsArgs(
    val title: String,
    val problemId: Int
)