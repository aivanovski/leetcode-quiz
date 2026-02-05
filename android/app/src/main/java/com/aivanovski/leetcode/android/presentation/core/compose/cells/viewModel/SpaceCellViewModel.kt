package com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.SpaceCellModel

@Immutable
class SpaceCellViewModel(
    override val model: SpaceCellModel
) : CellViewModel