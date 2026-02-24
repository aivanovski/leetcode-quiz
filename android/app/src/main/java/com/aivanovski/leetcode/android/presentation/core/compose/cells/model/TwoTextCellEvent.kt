package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEvent

sealed interface TwoTextCellEvent : CellEvent {
    data class OnClick(
        val cellId: String
    ) : TwoTextCellEvent
}