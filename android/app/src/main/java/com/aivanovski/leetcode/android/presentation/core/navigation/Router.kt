package com.aivanovski.leetcode.android.presentation.core.navigation

import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.root.model.NavBackStack
import java.util.LinkedList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface Router {
    fun navigateBack()
    fun setRoot(screen: Screen)
    fun navigateTo(screen: Screen)
    fun flow(): StateFlow<NavBackStack>
}

class RouterImpl : Router {

    private val backStack = MutableStateFlow(NavBackStack.from(Screen.Quiz))

    override fun navigateBack() {
        val screens = LinkedList(backStack.value.stack)
        if (screens.size > 1) {
            screens.removeLast()
        }

        backStack.value = NavBackStack(screens)
    }

    override fun setRoot(screen: Screen) {
        backStack.value = NavBackStack.from(screen)
    }

    override fun navigateTo(screen: Screen) {
        backStack.value = NavBackStack(backStack.value.stack.plus(screen))
    }

    override fun flow(): StateFlow<NavBackStack> = backStack
}