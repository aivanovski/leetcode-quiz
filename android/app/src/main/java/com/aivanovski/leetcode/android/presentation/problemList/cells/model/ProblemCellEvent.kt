package com.aivanovski.leetcode.android.presentation.problemList.cells.model

import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEvent

sealed interface ProblemCellEvent : CellEvent {
    data class OnClick(
        val problemId: Int
    ) : ProblemCellEvent
}