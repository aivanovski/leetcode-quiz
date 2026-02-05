package com.aivanovski.leetcode.android.presentation.problemDetails.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.entity.Problem

@Immutable
data class ProblemDetailsData(
    val problem: Problem,
    val htmlContent: String
)