package com.aivanovski.leetcode.android.presentation.core.mvvm

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aivanovski.leetcode.android.utils.mutableStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class MviViewModel<State, Intent>(
    initialState: State,
    private val initialIntent: Intent
) : ViewModel(), ScreenViewModel {

    val state = MutableStateFlow(initialState)
    private val intents = Channel<Intent>(capacity = Channel.BUFFERED)
    private var isStarted by mutableStateFlow(false)
    private val blocks = mutableListOf<() -> Unit>()

    @OptIn(ExperimentalCoroutinesApi::class)
    @CallSuper
    override fun start() {
        if (!isStarted) {
            isStarted = true

            for (block in blocks) {
                block.invoke()
            }
            blocks.clear()
        }

        doOnceWhenStarted {
            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(initialIntent) }
                    .flatMapLatest { intent -> handleIntent(intent) }
                    .flowOn(Dispatchers.IO)
                    .collect { newState ->
                        state.value = newState
                    }
            }
        }
    }

    @CallSuper
    override fun destroy() {
        isStarted = false
    }

    fun sendIntent(intent: Intent) {
        viewModelScope.launch {
            intents.send(intent)
        }
    }

    protected fun doOnceWhenStarted(block: () -> Unit) {
        if (isStarted) {
            block.invoke()
        } else {
            blocks.add(block)
        }
    }

    abstract fun handleIntent(intent: Intent): Flow<State>
}