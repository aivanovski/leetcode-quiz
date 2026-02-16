package com.aivanovski.leetcode.android.presentation.root

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.NavDisplay
import com.aivanovski.leetcode.android.App
import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.core.compose.SingleEventEffect
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.QuarterMargin
import com.aivanovski.leetcode.android.presentation.core.navigation.NavigationEvent
import com.aivanovski.leetcode.android.presentation.navigationRoutes
import com.aivanovski.leetcode.android.presentation.root.model.BottomNavItem
import com.aivanovski.leetcode.android.presentation.root.model.NavBackStack

@Composable
fun RootScreen(viewModel: RootViewModel) {
    val backStack by viewModel.backStack.collectAsStateWithLifecycle()
    val isBottomBarVisible by viewModel.isBottomBarVisible.collectAsStateWithLifecycle()
    val selectedNavigationItem by viewModel.selectedBottomBarIndex.collectAsStateWithLifecycle()

    RootScreenContent(
        backStack = backStack,
        isBottomBarVisible = isBottomBarVisible,
        selectedBottomBarIndex = selectedNavigationItem,
        onBottomBarClick = viewModel::onBottomBarClicked,
        onBackClick = viewModel::onBackClick
    )

    val activity = LocalContext.current as? Activity
    SingleEventEffect(
        eventFlow = viewModel.events,
        collector = { event ->
            when (event) {
                NavigationEvent.ExitApplication -> {
                    activity?.finish()
                }
            }
        }
    )

    // TODO: refactor
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
    isBottomBarVisible: Boolean,
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
            if (isBottomBarVisible) {
                BottomBarContent(
                    selectedIndex = selectedBottomBarIndex,
                    onBottomBarClick = onBottomBarClick
                )
            }
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
            if (backStack.stack.isNotEmpty()) {
                NavDisplay(
                    backStack = backStack.stack,
                    onBack = onBackClick,
                    entryProvider = navEntryProvider,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun BottomBarContent(
    selectedIndex: Int,
    onBottomBarClick: (index: Int) -> Unit
) {
    NavigationBar(
        tonalElevation = 1.dp
    ) {
        for ((index, item) in BottomNavItem.ALL_ITEMS.withIndex()) {
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = (selectedIndex == index),
                onClick = {
                    onBottomBarClick.invoke(index)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppTheme.colors.secondary,
                    unselectedIconColor = AppTheme.colors.secondary,
                    selectedTextColor = AppTheme.colors.secondary,
                    unselectedTextColor = AppTheme.colors.secondary,
                    indicatorColor = AppTheme.colors.selection
                )
            )
        }
    }
}

@Preview
@Composable
fun RootScreenPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        RootScreenContent(
            backStack = NavBackStack(emptyList()),
            isBottomBarVisible = true,
            selectedBottomBarIndex = 0,
            onBottomBarClick = {},
            onBackClick = {}
        )
    }
}