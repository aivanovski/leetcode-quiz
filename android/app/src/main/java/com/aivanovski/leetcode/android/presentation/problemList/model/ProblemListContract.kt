package com.aivanovski.leetcode.android.presentation.problemList.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel

sealed interface ProblemListIntent {
    data object Initialize : ProblemListIntent
    data object Refresh : ProblemListIntent
}

@Immutable
sealed interface ProblemListState {

    @Immutable
    data object Loading : ProblemListState

    @Immutable
    data class Error(
        val message: ErrorMessage
    ) : ProblemListState

    @Immutable
    data class Data(
        val cellViewModels: List<CellViewModel>
    ) : ProblemListState
}