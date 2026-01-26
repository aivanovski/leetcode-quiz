package com.aivanovski.leetcode.android.presentation.problemDetails.cells.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellModel

@Immutable
data class ProblemHintsCellModel(
    override val id: String,
    val hints: List<String>
) : CellModel