package com.aivanovski.leetcode.android.presentation.core.mvvm

import androidx.compose.runtime.Stable

@Stable
interface ScreenViewModel {
    fun start()
    fun destroy()
}