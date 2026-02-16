package com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TwoTextCellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TwoTextCellModel

@Immutable
class TwoTextCellViewModel(
    override val model: TwoTextCellModel,
    private val eventProvider: CellEventProvider
) : CellViewModel {

    fun sendEvent(event: TwoTextCellEvent) {
        eventProvider.sendEvent(event)
    }
}