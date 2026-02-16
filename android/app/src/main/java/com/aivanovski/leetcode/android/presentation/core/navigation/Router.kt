package com.aivanovski.leetcode.android.presentation.core.navigation

import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.root.model.NavBackStack
import java.util.LinkedList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

interface Router {
    fun navigateBack()
    fun setRoot(screen: Screen)
    fun navigateTo(screen: Screen)
    fun getNavigationFlow(): StateFlow<NavBackStack>
    fun getEventsFlow(): Flow<NavigationEvent>
}

sealed interface NavigationEvent {
    data object ExitApplication : NavigationEvent
}

class RouterImpl : Router {

    private val backStack = MutableStateFlow(NavBackStack(emptyList()))
    private val events = Channel<NavigationEvent>(capacity = Channel.BUFFERED)

    override fun navigateBack() {
        val screens = LinkedList(backStack.value.stack)
        if (screens.size > 1) {
            screens.removeLast()
            backStack.value = NavBackStack(screens)
        } else {
            events.trySend(NavigationEvent.ExitApplication)
        }
    }

    override fun setRoot(screen: Screen) {
        backStack.value = NavBackStack.from(screen)
    }

    override fun navigateTo(screen: Screen) {
        backStack.value = NavBackStack(backStack.value.stack.plus(screen))
    }

    override fun getNavigationFlow(): StateFlow<NavBackStack> = backStack

    override fun getEventsFlow(): Flow<NavigationEvent> = events.receiveAsFlow()
}