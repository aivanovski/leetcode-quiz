package com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.EmptyMessageCellModel

@Immutable
class EmptyMessageCellViewModel(
    override val model: EmptyMessageCellModel
) : CellViewModel