package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEvent

sealed interface DropDownCellEvent : CellEvent {
    data class OnOptionSelect(
        val cellId: String,
        val selectedOption: String
    ) : DropDownCellEvent
}