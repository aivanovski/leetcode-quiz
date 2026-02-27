package com.aivanovski.leetcode.android.entity

import androidx.compose.runtime.Immutable

@Immutable
data class Problem(
    val id: Int,
    val title: String,
    val categoryTitle: String,
    val difficulty: String,
    val url: String
)