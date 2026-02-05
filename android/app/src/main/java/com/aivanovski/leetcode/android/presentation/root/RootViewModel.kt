package com.aivanovski.leetcode.android.presentation.root

import androidx.lifecycle.ViewModel
import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.core.navigation.Router
import kotlinx.coroutines.flow.MutableStateFlow

class RootViewModel(
    private val router: Router
) : ViewModel() {

    val backStack = router.flow()
    val selectedBottomBarIndex = MutableStateFlow(0)

    fun start() {
        router.setRoot(determineScreen(selectedBottomBarIndex.value))
    }

    fun onBottomBarClicked(index: Int) {
        selectedBottomBarIndex.value = index

        router.setRoot(determineScreen(index))
    }

    fun onBackClick() {
        router.navigateBack()
    }

    fun navigateTo(screen: Screen) {
        router.navigateTo(screen)
    }

    private fun determineScreen(selectedBottomBarIndex: Int): Screen {
        return when (selectedBottomBarIndex) {
            0 -> Screen.Quiz
            1 -> Screen.ProblemList
            2 -> Screen.Settings
            else -> Screen.Quiz
        }
    }
}