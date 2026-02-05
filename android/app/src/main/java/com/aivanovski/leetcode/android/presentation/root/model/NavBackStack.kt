package com.aivanovski.leetcode.android.presentation.root.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.Screen

@Immutable
data class NavBackStack(
    val stack: List<Screen>
) {

    companion object {
        fun from(screen: Screen) = NavBackStack(stack = listOf(screen))
    }
}