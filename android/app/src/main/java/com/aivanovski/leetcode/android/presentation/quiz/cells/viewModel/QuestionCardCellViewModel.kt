package com.aivanovski.leetcode.android.presentation.quiz.cells.viewModel

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.quiz.cells.model.QuestionCardCellEvent
import com.aivanovski.leetcode.android.presentation.quiz.cells.model.QuestionCardCellModel

@Immutable
class QuestionCardCellViewModel(
    override val model: QuestionCardCellModel,
    private val eventProvider: CellEventProvider
) : CellViewModel {

    fun sendEvent(event: QuestionCardCellEvent) {
        eventProvider.sendEvent(event)
    }
}