package com.aivanovski.leetcode.android.presentation.problemList.cells.viewModel

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.problemList.cells.model.ProblemCellEvent
import com.aivanovski.leetcode.android.presentation.problemList.cells.model.ProblemCellModel

@Immutable
class ProblemCellViewModel(
    override val model: ProblemCellModel,
    private val eventProvider: CellEventProvider
) : CellViewModel {

    fun sendEvent(event: ProblemCellEvent) {
        eventProvider.sendEvent(event)
    }
}