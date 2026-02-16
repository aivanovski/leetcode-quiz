package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellModel

data class SecretFieldCellModel(
    override val id: String,
    val value: String,
    val label: String,
    val isTextVisible: Boolean
) : CellModel