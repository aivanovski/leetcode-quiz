package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellModel

@Immutable
data class HeaderCellModel(
    override val id: String,
    val text: String,
    val textSize: TextSize,
    val textColor: Color
) : CellModel