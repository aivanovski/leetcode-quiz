package com.aivanovski.leetcode.android.presentation.root

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aivanovski.leetcode.android.di.GlobalInjector.inject
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme

class RootActivity : ComponentActivity() {

    private val viewModel: RootViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                RootScreen(viewModel)
            }
        }
    }
}