package com.aivanovski.leetcode.android.presentation.problemDetails.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel

@Immutable
sealed interface ProblemDetailsState {

    @Immutable
    data object Loading : ProblemDetailsState

    @Immutable
    data class Error(
        val message: String
    ) : ProblemDetailsState

    @Immutable
    data class Data(
        val cellViewModels: List<CellViewModel>
    ) : ProblemDetailsState
}