package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellModel
import com.aivanovski.leetcode.android.presentation.core.compose.icons.VectorIcon

data class TextFieldCellModel(
    override val id: String,
    val value: String,
    val label: String,
    val icon: VectorIcon?
) : CellModel