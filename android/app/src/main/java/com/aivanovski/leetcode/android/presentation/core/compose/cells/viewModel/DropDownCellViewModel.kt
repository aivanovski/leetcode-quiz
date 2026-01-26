package com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel

import androidx.compose.runtime.Stable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.cells.MutableCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.DropDownCellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.DropDownCellModel

@Stable
class DropDownCellViewModel(
    model: DropDownCellModel,
    private val eventProvider: CellEventProvider
) : MutableCellViewModel<DropDownCellModel>(model) {

    fun sendEvent(event: DropDownCellEvent) {
        handleEvent(event)
        eventProvider.sendEvent(event)
    }

    private fun handleEvent(event: DropDownCellEvent) {
        when (event) {
            is DropDownCellEvent.OnOptionSelect -> {
                observableModel.value = observableModel.value.copy(
                    selectedOption = event.selectedOption
                )
            }
        }
    }
}