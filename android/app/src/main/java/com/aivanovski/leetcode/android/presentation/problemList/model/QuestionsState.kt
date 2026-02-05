package com.aivanovski.leetcode.android.presentation.problemList.model

import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.presentation.problemList.cells.viewModel.ProblemCellViewModel

sealed class QuestionsState {
    data object Loading : QuestionsState()
    data class Error(
        val message: ErrorMessage
    ) : QuestionsState()
    data class Data(
        val cellViewModels: List<ProblemCellViewModel>
    ) : QuestionsState()
}