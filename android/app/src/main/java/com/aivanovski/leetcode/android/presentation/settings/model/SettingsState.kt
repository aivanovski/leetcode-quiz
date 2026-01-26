package com.aivanovski.leetcode.android.presentation.settings.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel

@Immutable
sealed interface SettingsState {

    @Immutable
    data object Loading : SettingsState

    @Immutable
    data class Data(
        val cellViewModels: List<CellViewModel>
    ) : SettingsState
}