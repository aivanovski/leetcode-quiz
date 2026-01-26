package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEvent

sealed interface MenuCellEvent : CellEvent {
    data class OnClick(
        val cellId: String
    ) : MenuCellEvent
}