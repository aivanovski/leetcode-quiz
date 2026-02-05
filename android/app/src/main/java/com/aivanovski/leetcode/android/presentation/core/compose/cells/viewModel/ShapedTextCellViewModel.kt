package com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.ShapedTextCellModel

@Immutable
class ShapedTextCellViewModel(
    override val model: ShapedTextCellModel
) : CellViewModel