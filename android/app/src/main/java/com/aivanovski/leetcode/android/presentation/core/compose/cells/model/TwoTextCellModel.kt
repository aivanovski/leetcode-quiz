package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.TextColor
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellModel

@Immutable
data class TwoTextCellModel(
    override val id: String,
    val primaryText: String,
    val secondaryText: String,
    val primaryTextSize: TextSize,
    val secondaryTextSize: TextSize,
    val primaryTextColor: TextColor,
    val secondaryTextColor: TextColor
) : CellModel