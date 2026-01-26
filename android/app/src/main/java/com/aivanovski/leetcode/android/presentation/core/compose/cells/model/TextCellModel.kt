package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.TextColor
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellModel

@Immutable
data class TextCellModel(
    override val id: String,
    val text: String,
    val textSize: TextSize,
    val textColor: TextColor
) : CellModel