package com.aivanovski.leetcode.android.presentation.core.mvvm

import androidx.compose.runtime.Stable

@Stable
interface ScreenViewModel {
    fun create() {}
    fun start() {}
    fun destroy() {}
}