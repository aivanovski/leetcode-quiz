package com.aivanovski.leetcode.android.presentation.problemList.cells.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellModel

@Immutable
data class ProblemCellModel(
    override val id: String,
    val problemId: Int,
    val number: String,
    val title: String,
    val categoryTitle: String,
    val difficulty: String,
    val likes: String,
    val acceptanceRate: String,
    val submissions: String
) : CellModel