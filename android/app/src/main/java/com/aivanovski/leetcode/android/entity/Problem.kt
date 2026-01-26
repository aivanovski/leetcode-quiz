package com.aivanovski.leetcode.android.entity

import androidx.compose.runtime.Immutable

@Immutable
data class Problem(
    val id: Int,
    val title: String,
    val content: String,
    val hints: List<String>,
    val categoryTitle: String,
    val difficulty: String,
    val url: String,
    val likes: Long,
    val dislikes: Long
)