package com.aivanovski.leetcode.android.presentation.quizStart.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.entity.ErrorMessage

sealed interface QuizStartIntent {
    data object Initialize : QuizStartIntent
    data object OnRefresh : QuizStartIntent
    data object OnStartClick : QuizStartIntent
    data class OnErrorAction(val actionId: Int) : QuizStartIntent
}

@Immutable
sealed interface QuizStartState {

    @Immutable
    data object Loading : QuizStartState

    @Immutable
    data class Error(
        val message: ErrorMessage
    ) : QuizStartState

    @Immutable
    data class Data(
        val title: String,
        val buttonText: String
    ) : QuizStartState
}