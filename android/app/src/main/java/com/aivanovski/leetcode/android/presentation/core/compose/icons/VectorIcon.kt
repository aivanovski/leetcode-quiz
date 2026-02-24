package com.aivanovski.leetcode.android.presentation.core.compose.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector

enum class VectorIcon(
    val vector: ImageVector
) {
    BACK(Icons.AutoMirrored.Filled.ArrowBack),
    LIST(Icons.AutoMirrored.Filled.List),
    FAVORITE(Icons.Filled.Favorite),
    QUIZ(Icons.Filled.Quiz),
    CLOSE(Icons.Filled.Close),
    ARROW_DROP_DOWN(Icons.Filled.ArrowDropDown),
    ARROW_DROP_UP(Icons.Filled.ArrowDropUp),
    COLLAPSE_DOWN(Icons.Filled.KeyboardArrowDown),
    COLLAPSE_UP(Icons.Filled.KeyboardArrowUp),
    SETTINGS(Icons.Filled.Settings),
    VISIBILITY_OFF(Icons.Outlined.VisibilityOff),
    VISIBILITY_ON(Icons.Outlined.Visibility),
    ERROR_CIRCLE(Icons.Outlined.ErrorOutline)
}