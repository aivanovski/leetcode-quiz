package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEvent

sealed interface SecretFieldCellEvent : CellEvent {

    data class OnIconClick(
        val cellId: String
    ) : SecretFieldCellEvent

    data class OnTextChange(
        val cellId: String,
        val value: String
    ) : SecretFieldCellEvent
}