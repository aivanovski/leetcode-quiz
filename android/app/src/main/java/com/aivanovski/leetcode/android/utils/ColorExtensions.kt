package com.aivanovski.leetcode.android.utils

import androidx.compose.ui.graphics.Color

fun Color.toHexFormat(): String = "0x${value.toHexString()}"