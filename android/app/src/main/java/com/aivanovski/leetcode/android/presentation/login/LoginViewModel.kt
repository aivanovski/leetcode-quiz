package com.aivanovski.leetcode.android.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import arrow.core.Either
import com.aivanovski.leetcode.android.di.GlobalInjector
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProviderImpl
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.ButtonCellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.SecretFieldCellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextChipRowCellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextFieldCellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.SecretFieldCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.TextFieldCellViewModel
import com.aivanovski.leetcode.android.presentation.core.mvvm.MviViewModel
import com.aivanovski.leetcode.android.presentation.core.navigation.Router
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider
import com.aivanovski.leetcode.android.presentation.login.LoginCellFactory.LoginCellId
import com.aivanovski.leetcode.android.presentation.login.model.LoginIntent
import com.aivanovski.leetcode.android.presentation.login.model.LoginState
import com.aivanovski.leetcode.android.utils.formatReadableMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

class LoginViewModel(
    private val interactor: LoginInteractor,
    private val cellFactory: LoginCellFactory,
    private val router: Router,
    private val resources: ResourceProvider
) : MviViewModel<LoginState, LoginIntent>(
    initialState = LoginState.Loading,
    initialIntent = LoginIntent.Initialize
) {

    private val eventProvider: CellEventProvider = CellEventProviderImpl()
    private val debugCredentials = interactor.getDebugCredentials()

    init {
        subscribeToCellEvents()
    }

    override fun onCleared() {
        unsubscribeFromCellEvents()
    }

    override fun handleIntent(intent: LoginIntent): Flow<LoginState> {
        return when (intent) {
            LoginIntent.Initialize -> flowOf(createInitialState())
            LoginIntent.OnLoginClick -> login()
            LoginIntent.OnBackClick -> {
                router.navigateBack()
                emptyFlow()
            }
        }
    }

    private fun login(): Flow<LoginState> {
        val emailViewModel = getEmailViewModel() ?: return emptyFlow()
        val passwordViewModel = getPasswordViewModel() ?: return emptyFlow()

        val prevState = state.value as LoginState.Data

        return channelFlow {
            send(LoginState.Loading)

            val loginResult = interactor.login(
                email = emailViewModel.model.value.trim(),
                password = passwordViewModel.model.value.trim()
            )

            when (loginResult) {
                is Either.Left -> {
                    send(prevState.copy(errorMessage = createErrorMessage(loginResult.value)))
                }

                is Either.Right -> {
                    router.setRoot(Screen.QuizStart)
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun subscribeToCellEvents() {
        eventProvider.subscribe(this) { event ->
            when {
                event is ButtonCellEvent.OnClick &&
                    event.cellId == LoginCellId.LOGIN_BUTTON.id -> {
                    sendIntent(LoginIntent.OnLoginClick)
                }

                event is TextChipRowCellEvent.OnClick -> {
                    val credentials = debugCredentials.getOrNull(event.chipIndex)
                    if (credentials != null) {
                        getEmailViewModel()
                            ?.sendEvent(
                                TextFieldCellEvent.OnTextChange(
                                    cellId = LoginCellId.EMAIL.id,
                                    value = credentials.email
                                )
                            )
                        getPasswordViewModel()
                            ?.sendEvent(
                                SecretFieldCellEvent.OnTextChange(
                                    cellId = LoginCellId.PASSWORD.id,
                                    value = credentials.password
                                )
                            )
                    }
                }
            }
        }
    }

    private fun unsubscribeFromCellEvents() {
        eventProvider.unsubscribe(this)
    }

    private fun createErrorMessage(error: AppException): ErrorMessage {
        Timber.i(error)

        return ErrorMessage(
            message = error.formatReadableMessage(resources)
        )
    }

    private fun createInitialState(): LoginState =
        LoginState.Data(
            cellViewModels = cellFactory.createCells(debugCredentials, eventProvider)
        )

    private fun findCellViewModel(cellId: LoginCellId): CellViewModel? {
        return (state.value as? LoginState.Data)
            ?.cellViewModels
            ?.firstOrNull { cellViewModel -> cellViewModel.model.id == cellId.id }
    }

    private fun getPasswordViewModel(): SecretFieldCellViewModel? =
        findCellViewModel(LoginCellId.PASSWORD) as? SecretFieldCellViewModel

    private fun getEmailViewModel(): TextFieldCellViewModel? =
        findCellViewModel(LoginCellId.EMAIL) as? TextFieldCellViewModel

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GlobalInjector.get<LoginViewModel>() as T
        }
    }

    companion object {
        private const val ACTION_RETRY = 1
    }
}