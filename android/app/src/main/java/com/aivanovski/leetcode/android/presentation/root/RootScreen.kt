package com.aivanovski.leetcode.android.presentation.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.ui.NavDisplay
import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.navigationRoutes
import com.aivanovski.leetcode.android.presentation.root.model.BottomNavItem
import com.aivanovski.leetcode.android.presentation.root.model.NavBackStack

@Composable
fun RootScreen(viewModel: RootViewModel) {
    val backStack by viewModel.backStack.collectAsState()
    val selectedNavigationItem by viewModel.selectedBottomBarIndex.collectAsState()

    RootScreenContent(
        backStack = backStack,
        selectedBottomBarIndex = selectedNavigationItem,
        onBottomBarClick = viewModel::onBottomBarClicked,
        onBackClick = viewModel::onBackClick
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.start()
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
private fun RootScreenContent(
    backStack: NavBackStack,
    selectedBottomBarIndex: Int,
    onBottomBarClick: (index: Int) -> Unit,
    onBackClick: () -> Unit
) {
    val navEntryProvider = remember {
        { screen: Screen -> navigationRoutes(screen) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomBarContent(
                selectedIndex = selectedBottomBarIndex,
                onBottomBarClick = onBottomBarClick
            )
        }
    ) { padding ->
        Surface(
            color = AppTheme.theme.colors.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = padding.calculateBottomPadding()
                )
        ) {
            NavDisplay(
                backStack = backStack.stack,
                onBack = onBackClick,
                entryProvider = navEntryProvider,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun BottomBarContent(
    selectedIndex: Int,
    onBottomBarClick: (index: Int) -> Unit
) {
    NavigationBar {
        for ((index, item) in BottomNavItem.ALL_ITEMS.withIndex()) {
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = (selectedIndex == index),
                onClick = {
                    onBottomBarClick.invoke(index)
                }
            )
        }
    }
}

@Preview
@Composable
fun RootScreenPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        RootScreenContent(
            backStack = NavBackStack.from(Screen.ProblemList),
            selectedBottomBarIndex = 0,
            onBottomBarClick = {},
            onBackClick = {}
        )
    }
}