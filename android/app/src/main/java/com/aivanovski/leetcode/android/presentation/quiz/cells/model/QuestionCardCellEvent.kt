package com.aivanovski.leetcode.android.presentation.quiz.cells.model

import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEvent

sealed interface QuestionCardCellEvent : CellEvent {
    data class OnClick(
        val cellId: String
    ) : QuestionCardCellEvent
}