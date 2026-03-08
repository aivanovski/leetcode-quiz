package com.aivanovski.leetcode.android.presentation.problemDetails.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel

sealed interface ProblemDetailsIntent {
    data object Initialize : ProblemDetailsIntent
    data object NavigateBack : ProblemDetailsIntent
    data class OnErrorAction(
        val actionId: Int
    ) : ProblemDetailsIntent
}

@Immutable
sealed interface ProblemDetailsState {

    @Immutable
    data object Loading : ProblemDetailsState

    @Immutable
    data class Error(
        val message: ErrorMessage
    ) : ProblemDetailsState

    @Immutable
    data class Data(
        val title: String,
        val cellViewModels: List<CellViewModel>
    ) : ProblemDetailsState
}