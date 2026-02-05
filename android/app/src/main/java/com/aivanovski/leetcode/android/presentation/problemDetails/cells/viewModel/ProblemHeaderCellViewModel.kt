package com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.model.ProblemHeaderCellModel

@Immutable
class ProblemHeaderCellViewModel(
    override val model: ProblemHeaderCellModel
) : CellViewModel