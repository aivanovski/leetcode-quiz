package com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.model.ProblemHintsCellModel

@Immutable
class ProblemHintsCellViewModel(
    override val model: ProblemHintsCellModel
) : CellViewModel