package com.aivanovski.leetcode.android.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aivanovski.leetcode.android.data.settings.Settings
import com.aivanovski.leetcode.android.di.GlobalInjector
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProviderImpl
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.DropDownCellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.SwitchCellEvent
import com.aivanovski.leetcode.android.presentation.core.navigation.Router
import com.aivanovski.leetcode.android.presentation.settings.SettingsCellFactory.SettingsCellId
import com.aivanovski.leetcode.android.presentation.settings.model.SettingsState
import io.ktor.client.plugins.logging.LogLevel
import kotlinx.coroutines.flow.MutableStateFlow

class SettingsViewModel(
    private val interactor: SettingsInteractor,
    private val cellFactory: SettingsCellFactory,
    private val settings: Settings,
    private val router: Router
) : ViewModel() {

    val state = MutableStateFlow<SettingsState>(SettingsState.Loading)

    private val eventProvider: CellEventProvider = CellEventProviderImpl()

    init {
        state.value = SettingsState.Data(
            cellFactory.createCells(
                settings = settings,
                eventProvider = eventProvider
            )
        )
        subscribeToCellEvents()
    }

    override fun onCleared() {
        unsubscribeFromCellEvents()
    }

    private fun subscribeToCellEvents() {
        eventProvider.subscribe(this) { event ->
            when (event) {
                is DropDownCellEvent.OnOptionSelect -> {
                    when (event.cellId) {
                        SettingsCellId.SERVER_URL.id -> {
                            settings.serverUrl = event.selectedOption
                        }

                        SettingsCellId.HTTP_LOG_LEVEL.id -> {
                            settings.httpLogLevel =
                                LogLevel.entries.first { it.name == event.selectedOption }
                        }
                    }
                    interactor.reCreateHttpClient()
                }

                is SwitchCellEvent.OnCheckChanged -> {
                    settings.isValidateSslCertificate = event.isChecked
                    interactor.reCreateHttpClient()
                }
            }
        }
    }

    private fun unsubscribeFromCellEvents() {
        eventProvider.unsubscribe(this)
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GlobalInjector.get<SettingsViewModel>() as T
        }
    }
}