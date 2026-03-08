package com.aivanovski.leetcode.android.presentation.problemDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.di.GlobalInjector
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.entity.exception.NetworkException
import com.aivanovski.leetcode.android.presentation.core.mvvm.MviViewModel
import com.aivanovski.leetcode.android.presentation.core.navigation.Router
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsArgs
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsIntent
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsState
import com.aivanovski.leetcode.android.utils.formatReadableMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.parameter.parametersOf

class ProblemDetailsViewModel(
    private val interactor: ProblemDetailsInteractor,
    private val cellFactory: ProblemDetailsCellFactory,
    private val router: Router,
    private val resources: ResourceProvider,
    private val args: ProblemDetailsArgs
) : MviViewModel<ProblemDetailsState, ProblemDetailsIntent>(
    initialState = ProblemDetailsState.Loading,
    initialIntent = ProblemDetailsIntent.Initialize
) {

    override fun handleIntent(intent: ProblemDetailsIntent): Flow<ProblemDetailsState> {
        return when (intent) {
            ProblemDetailsIntent.Initialize -> loadData()
            ProblemDetailsIntent.NavigateBack -> {
                navigateBack()
                emptyFlow()
            }

            is ProblemDetailsIntent.OnErrorAction -> {
                when (intent.actionId) {
                    ACTION_RETRY -> loadData()
                    else -> emptyFlow()
                }
            }
        }
    }

    private fun loadData(): Flow<ProblemDetailsState> =
        channelFlow {
            send(ProblemDetailsState.Loading)

            interactor.loadData(args.problemId)
                .collectLatest { result ->
                    val state = result.fold(
                        ifLeft = { error ->
                            ProblemDetailsState.Error(message = createErrorMessage(error))
                        },
                        ifRight = { data ->
                            ProblemDetailsState.Data(
                                title = data.problem.problem.title,
                                cellViewModels = cellFactory.createCells(data)
                            )
                        }
                    )

                    send(state)
                }
        }.flowOn(Dispatchers.IO)

    private fun navigateBack() {
        router.navigateBack()
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

    companion object {
        private const val ACTION_RETRY = 1
    }
}