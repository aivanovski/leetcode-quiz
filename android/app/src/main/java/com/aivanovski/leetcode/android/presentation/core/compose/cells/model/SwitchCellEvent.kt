package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEvent

interface SwitchCellEvent : CellEvent {

    data class OnCheckChanged(
        val cellId: String,
        val isChecked: Boolean
    ) : SwitchCellEvent
}