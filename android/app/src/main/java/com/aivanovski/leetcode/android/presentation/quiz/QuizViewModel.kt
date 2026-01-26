package com.aivanovski.leetcode.android.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.di.GlobalInjector
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.entity.Question
import com.aivanovski.leetcode.android.entity.exception.ApiException
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProviderImpl
import com.aivanovski.leetcode.android.presentation.core.navigation.Router
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider
import com.aivanovski.leetcode.android.presentation.quiz.model.Answer
import com.aivanovski.leetcode.android.presentation.quiz.model.QuizData
import com.aivanovski.leetcode.android.presentation.quiz.model.QuizState
import com.aivanovski.leetcode.android.utils.formatReadableMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class QuizViewModel(
    private val interactor: QuizInteractor,
    private val cellFactory: QuizCellFactory,
    private val router: Router,
    private val resources: ResourceProvider
) : ViewModel() {

    val state = MutableStateFlow<QuizState>(QuizState.Loading)
    val isRefreshing = MutableStateFlow(false)

    private val eventProvider = CellEventProviderImpl()
    private var screenData: QuizData? = null
    private val queuedQuestionIds = HashSet<String>()
    private var currentQuestionId: String? = null
    private var answeredQuestionsIds = HashSet<String>()

    init {
        loadData(isSelectQuestion = true)
    }

    fun loadData(isSelectQuestion: Boolean = false) {
        viewModelScope.launch {
            interactor.loadData().fold(
                ifLeft = { error ->
                    isRefreshing.value = false
                    state.value = QuizState.Error(createErrorMessage(error))
                },
                ifRight = { data ->
                    isRefreshing.value = false
                    screenData = data
                    if (isSelectQuestion) {
                        currentQuestionId = findNextQuestion()?.id
                    }

                    rebuildScreenState()
                }
            )
        }
    }

    fun onRestartClicked() {
        answeredQuestionsIds.clear()
        currentQuestionId = null

        loadData(isSelectQuestion = true)
    }

    fun onRefresh() {
        isRefreshing.value = true
        answeredQuestionsIds.clear()
        loadData(isSelectQuestion = false)
    }

    fun onCardAnswered(answer: Answer) {
        val questionId = currentQuestionId ?: return
        val data = screenData ?: return

        if (queuedQuestionIds.size > 1) {
            state.value = QuizState.Loading
            return
        }

        queuedQuestionIds.add(questionId)
        answeredQuestionsIds.add(questionId)

        val nextQuestion = findNextQuestion()
        currentQuestionId = nextQuestion?.id

        rebuildScreenState()

        fetchMore(data.questionnaire.id, questionId, answer)
    }

    fun onErrorAction(actionId: Int) {
        when (actionId) {
            ACTION_RETRY -> loadData()
        }
    }

    private fun fetchMore(
        questionnaireId: String,
        questionId: String,
        answer: Answer
    ) {
        viewModelScope.launch {
            interactor.answerAndLoadMore(questionnaireId, questionId, answer).fold(
                ifLeft = { error ->
                    queuedQuestionIds.remove(questionId)
                    state.value = QuizState.Error(createErrorMessage(error))
                },
                ifRight = { data ->
                    queuedQuestionIds.remove(questionId)
                    screenData = data
                    rebuildScreenState()
                }
            )
        }
    }

    private fun rebuildScreenState() {
        val data = screenData ?: return
        val question = data.questionnaire.questions.firstOrNull { question ->
            question.id == currentQuestionId
        }

        viewModelScope.launch {
            if (question != null) {
                val cardViewModel = withContext(Dispatchers.IO) {
                    cellFactory.createQuestionCell(
                        question = question,
                        problemIdToProblemMap = data.problems.associateBy { problem -> problem.id },
                        eventProvider = eventProvider
                    )
                }

                val questionIndex = data.questionnaire.questions.indexOf(question)

                if (cardViewModel != null) {
                    state.value = QuizState.Card(
                        cardViewModel = cardViewModel,
                        questionNumber = data.questionnaire.stats.answered + questionIndex + 1,
                        totalQuestions = data.questionnaire.stats.totalQuestions
                    )
                }
            } else {
                state.value = QuizState.Result(
                    questionsAnswered = data.questionnaire.stats.answered,
                    positivelyAnswered = data.questionnaire.stats.answeredPositively,
                    negativelyAnswered = data.questionnaire.stats.answeredNegatively
                )
            }
        }
    }

    private fun findNextQuestion(): Question? {
        val questions = screenData?.questionnaire?.questions ?: return null

        return questions.firstOrNull { question ->
            question.id != currentQuestionId && question.id !in answeredQuestionsIds
        }
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
            return GlobalInjector.get<QuizViewModel>() as T
        }
    }

    companion object {
        private const val ACTION_RETRY = 1
    }
}