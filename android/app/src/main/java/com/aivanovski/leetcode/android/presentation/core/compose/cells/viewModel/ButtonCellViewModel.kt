package com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.ButtonCellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.ButtonCellModel

@Immutable
class ButtonCellViewModel(
    override val model: ButtonCellModel,
    private val eventProvider: CellEventProvider
) : CellViewModel {

    fun sendEvent(event: ButtonCellEvent) {
        eventProvider.sendEvent(event)
    }
}