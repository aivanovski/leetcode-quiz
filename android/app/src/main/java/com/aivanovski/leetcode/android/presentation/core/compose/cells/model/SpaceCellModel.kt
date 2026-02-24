package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellModel

@Immutable
data class SpaceCellModel(
    override val id: String,
    val height: Dp
) : CellModel