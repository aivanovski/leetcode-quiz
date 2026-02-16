package com.aivanovski.leetcode.android.presentation.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.core.navigation.Router
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RootViewModel(
    private val interactor: RootInteractor,
    private val router: Router
) : ViewModel() {

    val backStack = router.getNavigationFlow()
    val events = router.getEventsFlow()

    val selectedBottomBarIndex = MutableStateFlow(0)
    val isBottomBarVisible = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            router.getNavigationFlow().collect { navStack ->
                val currentScreen = navStack.stack.lastOrNull()

                isBottomBarVisible.value = (currentScreen != null
                    && currentScreen != Screen.Quiz
                    && currentScreen != Screen.Login)
            }
        }
    }

    fun start() {
        val startScreen = if (interactor.isLoggedIn()) {
            Screen.QuizStart
        } else {
            Screen.Login
        }

        router.setRoot(startScreen)
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