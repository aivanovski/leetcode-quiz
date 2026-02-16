package com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel

import androidx.compose.runtime.Stable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.cells.MutableCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.SecretFieldCellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.SecretFieldCellModel

@Stable
class SecretFieldCellViewModel(
    initialModel: SecretFieldCellModel,
    private val eventProvider: CellEventProvider
) : MutableCellViewModel<SecretFieldCellModel>(initialModel) {

    fun sendEvent(event: SecretFieldCellEvent) {
        eventProvider.sendEvent(event)

        when (event) {
            is SecretFieldCellEvent.OnTextChange -> {
                observableModel.value = observableModel.value.copy(
                    value = event.value
                )
            }

            is SecretFieldCellEvent.OnIconClick -> {
                val model = observableModel.value
                observableModel.value = model.copy(
                    isTextVisible = !model.isTextVisible
                )
            }
        }
    }
}