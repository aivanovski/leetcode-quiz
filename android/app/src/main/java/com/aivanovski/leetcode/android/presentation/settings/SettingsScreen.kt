package com.aivanovski.leetcode.android.presentation.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.data.api.ServerUrls
import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.core.compose.CenteredBox
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.DropDownCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.SwitchCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.ui.DropDownCell
import com.aivanovski.leetcode.android.presentation.core.compose.cells.ui.SpaceCell
import com.aivanovski.leetcode.android.presentation.core.compose.cells.ui.SwitchCell
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.DropDownCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.SpaceCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.SwitchCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.preview.PreviewEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.toTextStyle
import com.aivanovski.leetcode.android.presentation.settings.model.SettingsState

@Composable
fun SettingsScreen(screen: Screen) {
    val factory = remember(screen) { SettingsViewModel.Factory() }
    val viewModel: SettingsViewModel = viewModel(factory = factory)

    val state by viewModel.state.collectAsState()

    SettingsScreenContent(
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(state: SettingsState) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = TextSize.TITLE_LARGE.toTextStyle(),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppTheme.materialColors.surface,
                    titleContentColor = AppTheme.materialColors.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                SettingsState.Loading -> {
                    CenteredBox { CircularProgressIndicator() }
                }

                is SettingsState.Data -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding()
                    ) {
                        items(state.cellViewModels) { model ->
                            RenderCell(model)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderCell(viewModel: CellViewModel) {
    when (viewModel) {
        is SpaceCellViewModel -> SpaceCell(viewModel)
        is SwitchCellViewModel -> SwitchCell(viewModel)
        is DropDownCellViewModel -> DropDownCell(viewModel)
        else -> throw IllegalArgumentException("Unknown cell: $viewModel")
    }
}

@Preview
@Composable
fun SettingsScreenPreview_Data() {
    ThemedScreenPreview(theme = LightTheme) {
        SettingsScreenContent(
            state = newDataState()
        )
    }
}

@Preview
@Composable
fun SettingsScreenPreview_Loading() {
    ThemedScreenPreview(theme = LightTheme) {
        SettingsScreenContent(
            state = newLoadingState()
        )
    }
}

private fun newLoadingState() = SettingsState.Loading

@Composable
private fun newDataState() =
    SettingsState.Data(
        cellViewModels = listOf(
            DropDownCellViewModel(
                model = DropDownCellModel(
                    id = "server_url",
                    title = stringResource(R.string.server_url),
                    options = listOf(ServerUrls.PROD_SERVER_URL),
                    selectedOption = ServerUrls.PROD_SERVER_URL
                ),
                eventProvider = PreviewEventProvider
            ),
            DropDownCellViewModel(
                model = DropDownCellModel(
                    id = "http_log_level",
                    title = stringResource(R.string.http_log_level),
                    options = listOf("INFO"),
                    selectedOption = "INFO"
                ),
                eventProvider = PreviewEventProvider
            ),
            SwitchCellViewModel(
                model = SwitchCellModel(
                    id = "ssl",
                    title = stringResource(R.string.validate_ssl_certificate_title),
                    description = stringResource(R.string.validate_ssl_certificate_description),
                    isChecked = true,
                    isEnabled = true
                ),
                eventProvider = PreviewEventProvider
            )
        )
    )