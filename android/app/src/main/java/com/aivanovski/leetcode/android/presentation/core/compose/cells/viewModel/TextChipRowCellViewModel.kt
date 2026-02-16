package com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextChipRowCellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextChipRowCellModel

@Immutable
class TextChipRowCellViewModel(
    override val model: TextChipRowCellModel,
    private val eventProvider: CellEventProvider
) : CellViewModel {

    fun sendIntent(event: TextChipRowCellEvent) {
        eventProvider.sendEvent(event)
    }
}