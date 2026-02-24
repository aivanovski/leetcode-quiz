package com.aivanovski.leetcode.android.presentation.problemDetails.cells.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellModel

@Immutable
data class ProblemHeaderCellModel(
    override val id: String,
    val problemId: Int,
    val number: String,
    val title: String,
    val categoryTitle: String,
    val difficulty: String
) : CellModel