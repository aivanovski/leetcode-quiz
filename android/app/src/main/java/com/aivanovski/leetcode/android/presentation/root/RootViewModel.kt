package com.aivanovski.leetcode.android.presentation.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.core.navigation.Router
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RootViewModel(
    private val router: Router
) : ViewModel() {

    val backStack = router.flow()
    val selectedBottomBarIndex = MutableStateFlow(0)
    val isBottomBarVisible = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            router.flow().collect { navStack ->
                val isVisible = when (navStack.stack.last()) {
                    is Screen.Quiz -> false
                    else -> true
                }

                isBottomBarVisible.value = isVisible
            }
        }
    }

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

    private fun determineScreen(selectedBottomBarIndex: Int): Screen {
        return when (selectedBottomBarIndex) {
            0 -> Screen.QuizStart
            1 -> Screen.ProblemList
            2 -> Screen.Settings
            else -> Screen.QuizStart
        }
    }
}