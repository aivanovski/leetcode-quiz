package com.aivanovski.leetcode.android.presentation.core.compose.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class VectorIcon(
    val vector: ImageVector
) {
    LIST(Icons.AutoMirrored.Filled.List),
    FAVORITE(Icons.Filled.Favorite),
    QUIZ(Icons.Filled.Quiz),
    ARROW_DROP_DOWN(Icons.Filled.ArrowDropDown),
    ARROW_DROP_UP(Icons.Filled.ArrowDropUp),
    SETTINGS(Icons.Filled.Settings)
}