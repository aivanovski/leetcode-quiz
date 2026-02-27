package com.aivanovski.leetcode.android.presentation.problemDetails.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.entity.ProblemWithContent

@Immutable
data class ProblemDetailsData(
    val problem: ProblemWithContent,
    val htmlContent: String
)