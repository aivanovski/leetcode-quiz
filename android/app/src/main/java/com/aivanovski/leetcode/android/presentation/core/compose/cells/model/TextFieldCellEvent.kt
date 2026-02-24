package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEvent

sealed interface TextFieldCellEvent : CellEvent {

    data class OnIconClick(
        val cellId: String
    ) : TextFieldCellEvent

    data class OnTextChange(
        val cellId: String,
        val value: String
    ) : TextFieldCellEvent
}