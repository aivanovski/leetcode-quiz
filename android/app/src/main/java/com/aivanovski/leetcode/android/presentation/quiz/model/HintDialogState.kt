package com.aivanovski.leetcode.android.presentation.quiz.model

import androidx.compose.runtime.Immutable

@Immutable
data class HintDialogState(
    val hints: List<String>,
    val algorithmHint: String,
    val formula: String,
    val solutions: List<String>
)