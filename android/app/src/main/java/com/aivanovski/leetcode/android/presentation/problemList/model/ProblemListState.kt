package com.aivanovski.leetcode.android.presentation.problemList.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.presentation.problemList.cells.viewModel.ProblemCellViewModel

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
        val cellViewModels: List<ProblemCellViewModel>
    ) : ProblemListState
}