package com.aivanovski.leetcode.android.presentation.root.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.aivanovski.leetcode.android.presentation.core.compose.icons.VectorIcon

data class BottomNavItem(
    val title: String,
    val icon: ImageVector
) {

    companion object {
        val ALL_ITEMS = listOf(
            BottomNavItem("Quiz", VectorIcon.QUIZ.vector),
            BottomNavItem("Problems", VectorIcon.LIST.vector),
            BottomNavItem("Settings", VectorIcon.SETTINGS.vector)
        )
    }
}