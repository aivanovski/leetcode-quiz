package com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel

import androidx.compose.runtime.Stable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.cells.MutableCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextFieldCellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextFieldCellModel

@Stable
class TextFieldCellViewModel(
    initialModel: TextFieldCellModel,
    private val eventProvider: CellEventProvider
) : MutableCellViewModel<TextFieldCellModel>(initialModel) {

    fun sendEvent(event: TextFieldCellEvent) {
        eventProvider.sendEvent(event)

        when (event) {
            is TextFieldCellEvent.OnTextChange -> {
                observableModel.value = observableModel.value.copy(
                    value = event.value
                )
            }

            else -> {}
        }
    }
}