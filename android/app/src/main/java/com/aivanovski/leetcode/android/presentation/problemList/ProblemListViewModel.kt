package com.aivanovski.leetcode.android.presentation.problemList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import arrow.core.Either
import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.di.GlobalInjector
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.entity.exception.NetworkException
import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProviderImpl
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.mvvm.MviViewModel
import com.aivanovski.leetcode.android.presentation.core.navigation.Router
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsArgs
import com.aivanovski.leetcode.android.presentation.problemList.cells.model.ProblemCellEvent
import com.aivanovski.leetcode.android.presentation.problemList.model.ProblemListIntent
import com.aivanovski.leetcode.android.presentation.problemList.model.ProblemListState
import com.aivanovski.leetcode.android.utils.formatReadableMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ProblemListViewModel(
    private val router: Router,
    private val interactor: ProblemListInteractor,
    private val cellFactory: ProblemListCellFactory,
    private val resources: ResourceProvider
) : MviViewModel<ProblemListState, ProblemListIntent>(
    initialState = ProblemListState.Loading,
    initialIntent = ProblemListIntent.Initialize
) {

    val searchQuery = MutableStateFlow("")
    val isSearchActive = MutableStateFlow(false)
    val isRefreshing = MutableStateFlow(false)

    @Volatile
    private var allProblems: List<Problem> = emptyList()

    @Volatile
    private var problemIdToProblemMap: Map<Int, Problem> = emptyMap()
    private val eventProvider = CellEventProviderImpl()

    init {
        subscribeToEvents()
    }

    override fun onCleared() {
        super.onCleared()
        eventProvider.clear()
    }

    override fun handleIntent(intent: ProblemListIntent): Flow<ProblemListState> {
        return when (intent) {
            ProblemListIntent.Initialize -> loadData(isForceReload = false)
            ProblemListIntent.Refresh -> loadData(isForceReload = true)
        }
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

    private fun loadData(isForceReload: Boolean): Flow<ProblemListState> {
        return interactor.getProblems(isForceReload)
            .map { result ->
                when (result) {
                    is Either.Left -> {
                        isRefreshing.value = false
                        ProblemListState.Error(
                            message = createErrorMessage(result.value)
                        )
                    }

                    is Either.Right -> {
                        isRefreshing.value = false
                        allProblems = result.value
                        problemIdToProblemMap =
                            allProblems.associateBy { problem -> problem.id }

                        ProblemListState.Data(
                            cellViewModels = createCellViewModels(
                                filterProblems(result.value, searchQuery.value)
                            )
                        )
                    }
                }
            }
            .onStart { emit(ProblemListState.Loading) }
            .flowOn(Dispatchers.IO)
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
            ACTION_RETRY -> loadData(isForceReload = false)
        }
    }

    private fun showData() {
        if (allProblems.isEmpty()) return

        state.value = ProblemListState.Data(
            cellViewModels = createCellViewModels(
                problems = if (isSearchActive.value) {
                    filterProblems(allProblems, searchQuery.value)
                } else {
                    allProblems
                }
            )
        )
    }

    private fun createCellViewModels(problems: List<Problem>): List<CellViewModel> {
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