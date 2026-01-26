package com.aivanovski.leetcode.android.presentation.problemDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aivanovski.leetcode.android.di.GlobalInjector
import com.aivanovski.leetcode.android.presentation.core.navigation.Router
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsArgs
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

class ProblemDetailsViewModel(
    private val interactor: ProblemDetailsInteractor,
    private val cellFactory: ProblemDetailsCellFactory,
    private val router: Router,
    private val args: ProblemDetailsArgs
) : ViewModel() {

    val state = MutableStateFlow<ProblemDetailsState>(ProblemDetailsState.Loading)

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            state.value = ProblemDetailsState.Loading

            interactor.loadData(args.problemId.toString()).fold(
                ifLeft = { error ->
                    state.value = ProblemDetailsState.Error(error.message ?: "")
                },
                ifRight = { data ->
                    state.value = ProblemDetailsState.Data(
                        cellViewModels = cellFactory.createCells(data)
                    )
                }
            )
        }
    }

    fun navigateBack() {
        router.navigateBack()
    }

    class Factory(
        private val args: ProblemDetailsArgs
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GlobalInjector.get<ProblemDetailsViewModel>(
                params = parametersOf(args)
            ) as T
        }
    }
}