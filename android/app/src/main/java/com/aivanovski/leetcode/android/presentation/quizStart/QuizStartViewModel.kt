package com.aivanovski.leetcode.android.presentation.quizStart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.di.GlobalInjector
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.entity.exception.ApiException
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.core.mvvm.MviViewModel
import com.aivanovski.leetcode.android.presentation.core.navigation.Router
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider
import com.aivanovski.leetcode.android.presentation.quizStart.model.QuizStartData
import com.aivanovski.leetcode.android.presentation.quizStart.model.QuizStartIntent
import com.aivanovski.leetcode.android.presentation.quizStart.model.QuizStartState
import com.aivanovski.leetcode.android.utils.formatReadableMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

class QuizStartViewModel(
    private val interactor: QuizStartInteractor,
    private val resources: ResourceProvider,
    private val router: Router
) : MviViewModel<QuizStartState, QuizStartIntent>(
    initialState = QuizStartState.Loading,
    initialIntent = QuizStartIntent.Initialize
) {

    val isRefreshing = MutableStateFlow(false)

    override fun handleIntent(intent: QuizStartIntent): Flow<QuizStartState> =
        when (intent) {
            QuizStartIntent.Initialize -> loadData()
            QuizStartIntent.OnRefresh -> {
                isRefreshing.value = true
                loadData()
            }

            QuizStartIntent.OnStartClick -> {
                router.navigateTo(Screen.Quiz)
                emptyFlow()
            }

            is QuizStartIntent.OnErrorAction -> onErrorActionClicked(intent.actionId)
        }

    fun loadData(): Flow<QuizStartState> =
        channelFlow {
            interactor.loadQuestionnaire().fold(
                ifLeft = { error ->
                    isRefreshing.value = false
                    send(QuizStartState.Error(createErrorMessage(error)))
                },
                ifRight = {
                    isRefreshing.value = false
                    send(createDataState(it))
                }
            )
        }
            .flowOn(Dispatchers.IO)

    private fun onErrorActionClicked(actionId: Int): Flow<QuizStartState> {
        return when (actionId) {
            ACTION_RETRY -> loadData()
            else -> emptyFlow()
        }
    }

    private fun createDataState(data: QuizStartData): QuizStartState.Data {
        val questionnaire = data.questionnaire
        val isNewQuestionnaire = questionnaire.answers.isEmpty()

        return QuizStartState.Data(
            title = if (isNewQuestionnaire) {
                resources.getString(R.string.questions_with_count, questionnaire.questions.size)
            } else {
                val answeredQuestions = questionnaire.answers.size
                val allQuestions = questionnaire.questions.size
                resources.getString(
                    R.string.questions_answered_with_count,
                    answeredQuestions,
                    allQuestions
                )
            },
            buttonText = if (isNewQuestionnaire) {
                resources.getString(R.string.start)
            } else {
                resources.getString(R.string.continue_quiz)
            }
        )
    }

    private fun createErrorMessage(error: AppException): ErrorMessage {
        Timber.i(error)

        return ErrorMessage(
            message = error.formatReadableMessage(resources),
            actionText = if (error is ApiException) {
                resources.getString(R.string.reload)
            } else {
                null
            },
            actionId = if (error is ApiException) {
                ACTION_RETRY
            } else {
                null
            }
        )
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GlobalInjector.get<QuizStartViewModel>() as T
        }
    }

    companion object {
        private const val ACTION_RETRY = 1
    }
}