package com.aivanovski.leetcode.android.presentation.login.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel

@Immutable
sealed interface LoginState {

    @Immutable
    data object Loading : LoginState

    @Immutable
    data class Data(
        val cellViewModels: List<CellViewModel> = emptyList(),
        val errorMessage: ErrorMessage? = null
    ) : LoginState
}

sealed interface LoginIntent {
    data object Initialize : LoginIntent
    data object OnLoginClick : LoginIntent
    data object OnBackClick : LoginIntent
}