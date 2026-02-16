package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEvent

interface TextChipRowCellEvent : CellEvent {
    data class OnClick(
        val chipIndex: Int
    ) : TextChipRowCellEvent
}