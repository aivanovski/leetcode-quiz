package com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.MenuCellModel

@Immutable
class MenuCellViewModel(
    override val model: MenuCellModel,
    private val eventProvider: CellEventProvider
) : CellViewModel {

    fun sendEvent(event: CellEvent) {
        eventProvider.sendEvent(event)
    }
}