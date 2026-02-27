package com.aivanovski.leetcode.android.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.di.GlobalInjector
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.entity.ProblemWithContent
import com.aivanovski.leetcode.android.entity.Question
import com.aivanovski.leetcode.android.entity.Questionnaire
import com.aivanovski.leetcode.android.entity.exception.ApiException
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProviderImpl
import com.aivanovski.leetcode.android.presentation.core.navigation.Router
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider
import com.aivanovski.leetcode.android.presentation.quiz.model.Answer
import com.aivanovski.leetcode.android.presentation.quiz.model.HintDialogState
import com.aivanovski.leetcode.android.presentation.quiz.model.QuizState
import com.aivanovski.leetcode.android.utils.formatReadableMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModel(
    private val interactor: QuizInteractor,
    private val cellFactory: QuizCellFactory,
    private val router: Router,
    private val resources: ResourceProvider
) : ViewModel() {

    val state = MutableStateFlow<QuizState>(QuizState.Loading)
    val isRefreshing = MutableStateFlow(false)

    private val loadProblemChannel = Channel<Int>(capacity = Channel.BUFFERED)
    private val eventProvider = CellEventProviderImpl()
    private var questionnaire: Questionnaire? = null
    private val queuedQuestionIds = HashSet<String>()
    private var currentQuestion: Question? = null
    private var answeredQuestionsIds = HashSet<String>()
    private var problemIdToProblemMap = HashMap<Int, ProblemWithContent>()
    private var answers = Answers(0, 0)

    init {
        viewModelScope.launch {
            loadProblemChannel.receiveAsFlow()
                .flatMapLatest { problemId -> interactor.loadProblem(problemId) }
                .flowOn(Dispatchers.IO)
                .collect { data -> onProblemLoaded(data) }
        }

        loadData(isSelectQuestion = true)
    }

    fun loadData(isSelectQuestion: Boolean = false) {
        viewModelScope.launch {
            interactor.loadQuestionnaire().fold(
                ifLeft = { error ->
                    isRefreshing.value = false
                    state.value = QuizState.Error(createErrorMessage(error))
                },
                ifRight = { questionnaire ->
                    onQuestionnaireLoaded(questionnaire, isSelectQuestion)
                }
            )
        }
    }

    fun onRestartClicked() {
        answeredQuestionsIds.clear()
        currentQuestion = null

        loadData(isSelectQuestion = true)
    }

    fun onRefresh() {
        isRefreshing.value = true
        answeredQuestionsIds.clear()
        loadData(isSelectQuestion = false)
    }

    fun onCardAnswered(answer: Answer) {
        val question = currentQuestion ?: return
        val questionnaire = questionnaire ?: return

        if (queuedQuestionIds.size > 1) {
            state.value = QuizState.Loading
            return
        }

        queuedQuestionIds.add(question.uid)
        answeredQuestionsIds.add(question.uid)

        currentQuestion = findNextQuestion()

        answers = when (answer) {
            Answer.GOOD -> answers.copy(positive = answers.positive + 1)
            Answer.BAD -> answers.copy(negative = answers.negative + 1)
        }

        rebuildScreenState()

        postAnswer(questionnaire.id, question.uid, answer)

        val problemId = currentQuestion?.problemId
        val problem = problemIdToProblemMap[problemId]
        if (problem == null && problemId != null) {
            loadProblemChannel.trySend(problemId)
        }
    }

    fun onHintButtonClicked() {
        val question = currentQuestion ?: return
        val problem = problemIdToProblemMap[question.problemId] ?: return

        state.value = state.value
            .asCard()
            .copy(
                hintDialogState = HintDialogState(
                    hints = problem.hints,
                    algorithmHint = question.question,
                    formula = "",
                    solutions = problem.solutions
                )
            )
    }

    fun onDismissHint() {
        state.value = state.value
            .asCard()
            .copy(
                hintDialogState = null
            )
    }

    fun onErrorAction(actionId: Int) {
        when (actionId) {
            ACTION_RETRY -> loadData()
        }
    }

    fun navigateBack() {
        router.navigateBack()
    }

    private fun onQuestionnaireLoaded(
        questionnaire: Questionnaire,
        isSelectQuestion: Boolean
    ) {
        this.questionnaire = questionnaire

        answers = answers.copy(
            positive = questionnaire.answers.count { it.answer == 1 },
            negative = questionnaire.answers.count { it.answer == -1 }
        )

        val answeredIds = questionnaire.answers.map { answer -> answer.questionId }
        answeredQuestionsIds.addAll(answeredIds)

        isRefreshing.value = false
        if (isSelectQuestion) {
            currentQuestion = findNextQuestion()
        }

        val problemId = currentQuestion?.problemId
        val problem = problemIdToProblemMap[problemId]
        if (problem == null && problemId != null) {
            loadProblemChannel.trySend(problemId)
        } else {
            rebuildScreenState()
        }
    }

    private fun postAnswer(
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
                    val answeredIds = data.answers.map { answer -> answer.questionId }
                    answeredQuestionsIds.addAll(answeredIds)
                    queuedQuestionIds.remove(questionId)
                    questionnaire = data
                    rebuildScreenState()
                }
            )
        }
    }

    private fun onProblemLoaded(data: Either<AppException, ProblemWithContent>) {
        data.fold(
            ifLeft = { error ->
                state.value = QuizState.Error(createErrorMessage(error))
            },
            ifRight = { data ->
                problemIdToProblemMap[data.problem.id] = data
                rebuildScreenState()
            }
        )
    }

    private fun rebuildScreenState() {
        val questionnaire = questionnaire ?: return
        val question = currentQuestion
        val problem = problemIdToProblemMap[question?.problemId]

        val answeredIds = questionnaire.answers.map { answer -> answer.questionId }
        val isAllAnswered = questionnaire.questions.all { question ->
            question.uid in answeredQuestionsIds || question.uid in answeredIds
        }

        viewModelScope.launch {
            if (question != null && problem != null) {
                val cardViewModel = withContext(Dispatchers.IO) {
                    cellFactory.createQuestionCell(
                        question = question,
                        problem = problem,
                        eventProvider = eventProvider
                    )
                }

                val questionIndex = questionnaire.questions.indexOfFirst { q ->
                    q.uid == question.uid
                }

                state.value = QuizState.Card(
                    cardViewModel = cardViewModel,
                    questionNumber = questionIndex + 1,
                    totalQuestions = questionnaire.stats.totalQuestions
                )
            } else if (isAllAnswered) {
                state.value = QuizState.Result(
                    questionsAnswered = questionnaire.questions.size,
                    positivelyAnswered = answers.positive,
                    negativelyAnswered = answers.negative
                )
            } else {
                state.value = QuizState.Loading
            }
        }
    }

    private fun findNextQuestion(): Question? {
        val questionnaire = questionnaire ?: return null

        return questionnaire.questions.firstOrNull { question ->
            question.uid != currentQuestion?.uid && question.uid !in answeredQuestionsIds
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

    private fun QuizState.asCard(): QuizState.Card = this as QuizState.Card

    private data class Answers(
        val positive: Int,
        val negative: Int
    )

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