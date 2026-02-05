package com.aivanovski.leetcode.android.presentation.settings

import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.data.api.ServerUrls
import com.aivanovski.leetcode.android.data.settings.Settings
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.DropDownCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.SwitchCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.DropDownCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.SwitchCellViewModel
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider
import io.ktor.client.plugins.logging.LogLevel

class SettingsCellFactory(
    private val resources: ResourceProvider
) {

    fun createCells(
        settings: Settings,
        eventProvider: CellEventProvider
    ): List<CellViewModel> {
        val cells = mutableListOf<CellViewModel>()

        cells.add(
            DropDownCellViewModel(
                DropDownCellModel(
                    id = SettingsCellId.SERVER_URL.name,
                    title = resources.getString(R.string.server_url),
                    options = listOf(
                        ServerUrls.PROD_SERVER_URL,
                        ServerUrls.INTERNAL_PROD_SERVER_URL,
                        ServerUrls.DEBUG_SERVER_URL
                    ),
                    selectedOption = settings.serverUrl
                ),
                eventProvider
            )
        )

        val logOptions = LogLevel.entries.map { level -> level.name }

        cells.add(
            DropDownCellViewModel(
                DropDownCellModel(
                    id = SettingsCellId.HTTP_LOG_LEVEL.name,
                    title = resources.getString(R.string.http_log_level),
                    options = logOptions,
                    selectedOption = settings.httpLogLevel.name
                ),
                eventProvider
            )
        )

        cells.add(
            SwitchCellViewModel(
                SwitchCellModel(
                    id = SettingsCellId.SSL_CERTIFICATE_SWITCH.name,
                    title = resources.getString(R.string.validate_ssl_certificate_title),
                    description = resources.getString(
                        R.string.validate_ssl_certificate_description
                    ),
                    isChecked = settings.isValidateSslCertificate,
                    isEnabled = true
                ),
                eventProvider
            )
        )

        return cells
    }

    enum class SettingsCellId {
        SERVER_URL,
        HTTP_LOG_LEVEL,
        SSL_CERTIFICATE_SWITCH;

        val id = name
    }
}