package com.aivanovski.leetcode.android.presentation.problemList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.data.api.ApiClient
import com.aivanovski.leetcode.android.di.GlobalInjector
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.entity.exception.NetworkException
import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProviderImpl
import com.aivanovski.leetcode.android.presentation.core.navigation.Router
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsArgs
import com.aivanovski.leetcode.android.presentation.problemList.cells.model.ProblemCellEvent
import com.aivanovski.leetcode.android.presentation.problemList.cells.viewModel.ProblemCellViewModel
import com.aivanovski.leetcode.android.presentation.problemList.model.QuestionsState
import com.aivanovski.leetcode.android.utils.formatReadableMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProblemListViewModel(
    private val router: Router,
    private val api: ApiClient,
    private val cellFactory: ProblemListCellFactory,
    private val resources: ResourceProvider
) : ViewModel() {

    val state = MutableStateFlow<QuestionsState>(QuestionsState.Loading)
    val searchQuery = MutableStateFlow("")
    val isSearchActive = MutableStateFlow(false)

    private var allProblems: List<Problem> = emptyList()
    private var problemIdToProblemMap: Map<Int, Problem> = emptyMap()
    private val eventProvider = CellEventProviderImpl()

    init {
        subscribeToEvents()
        loadQuestions()
    }

    override fun onCleared() {
        super.onCleared()
        eventProvider.unsubscribe(this)
        eventProvider.clear()
    }

    private fun subscribeToEvents() {
        eventProvider.subscribe(this) { event ->
            handleCellEvent(event)
        }
    }

    private fun handleCellEvent(event: CellEvent) {
        when (event) {
            is ProblemCellEvent.OnClick -> {
                navigateToProblemDetails(event.problemId)
            }
        }
    }

    fun loadQuestions() {
        viewModelScope.launch {
            state.value = QuestionsState.Loading

            api.getProblems().fold(
                ifLeft = { error ->
                    state.value = QuestionsState.Error(createErrorMessage(error))
                },
                ifRight = { problems ->
                    allProblems = problems
                    problemIdToProblemMap = allProblems.associateBy { problem -> problem.id }
                    state.value = QuestionsState.Data(
                        cellViewModels = createCellViewModels(
                            filterProblems(
                                problems,
                                searchQuery.value
                            )
                        )
                    )
                }
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
        isSearchActive.value = true
        showData()
    }

    fun onSearchClicked() {
        isSearchActive.value = true
    }

    fun onCloseSearch() {
        isSearchActive.value = false
        searchQuery.value = ""
        showData()
    }

    fun onErrorAction(actionId: Int) {
        when (actionId) {
            ACTION_RETRY -> loadQuestions()
        }
    }

    private fun showData() {
        if (allProblems.isEmpty()) return

        state.value = QuestionsState.Data(
            cellViewModels = createCellViewModels(
                problems = if (isSearchActive.value) {
                    filterProblems(allProblems, searchQuery.value)
                } else {
                    allProblems
                }
            )
        )
    }

    private fun createCellViewModels(problems: List<Problem>): List<ProblemCellViewModel> {
        return cellFactory.createProblemCells(problems, eventProvider)
    }

    private fun filterProblems(
        problems: List<Problem>,
        query: String
    ): List<Problem> {
        if (query.isBlank()) return problems

        val lowerQuery = query.lowercase()
        return problems.filter { problem ->
            problem.title.contains(lowerQuery, ignoreCase = true) ||
                problem.id.toString().contains(lowerQuery, ignoreCase = true) ||
                problem.categoryTitle.contains(lowerQuery, ignoreCase = true) ||
                problem.difficulty.contains(lowerQuery, ignoreCase = true)
        }
    }

    private fun navigateToProblemDetails(problemId: Int) {
        val problem = problemIdToProblemMap[problemId] ?: return

        router.navigateTo(
            Screen.ProblemDetails(
                ProblemDetailsArgs(
                    title = problem.title,
                    problemId = problemId
                )
            )
        )
    }

    private fun createErrorMessage(error: AppException): ErrorMessage {
        return ErrorMessage(
            message = error.formatReadableMessage(resources),
            actionText = if (error is NetworkException) {
                resources.getString(R.string.retry)
            } else {
                null
            },
            actionId = if (error is NetworkException) {
                ACTION_RETRY
            } else {
                null
            }
        )
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GlobalInjector.get<ProblemListViewModel>() as T
        }
    }

    companion object {
        private const val ACTION_RETRY = 1
    }
}