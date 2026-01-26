package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEvent

sealed interface ButtonCellEvent : CellEvent {
    data class OnClick(
        val cellId: String
    ) : ButtonCellEvent
}