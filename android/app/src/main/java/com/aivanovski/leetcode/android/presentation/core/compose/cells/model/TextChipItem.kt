package com.aivanovski.leetcode.android.presentation.core.compose.cells.model

import androidx.compose.ui.graphics.Color
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize

data class TextChipItem(
    val text: String,
    val textColor: Color,
    val textSize: TextSize,
    val isClickable: Boolean,
    val isSelected: Boolean
)