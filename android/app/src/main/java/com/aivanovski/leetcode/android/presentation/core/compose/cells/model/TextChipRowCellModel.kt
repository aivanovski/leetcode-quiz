package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellModel

data class TextChipRowCellModel(
    override val id: String,
    val chips: List<TextChipItem>
) : CellModel