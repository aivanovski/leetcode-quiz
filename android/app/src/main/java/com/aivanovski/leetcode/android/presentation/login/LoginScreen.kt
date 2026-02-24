package com.aivanovski.leetcode.android.presentation.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.core.compose.CenteredBox
import com.aivanovski.leetcode.android.presentation.core.compose.ErrorContent
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.ui.ButtonCell
import com.aivanovski.leetcode.android.presentation.core.compose.cells.ui.SecretFieldCell
import com.aivanovski.leetcode.android.presentation.core.compose.cells.ui.SpaceCell
import com.aivanovski.leetcode.android.presentation.core.compose.cells.ui.TextChipRowCell
import com.aivanovski.leetcode.android.presentation.core.compose.cells.ui.TextFieldCell
import com.aivanovski.leetcode.android.presentation.core.compose.cells.ui.newSecretFieldCell
import com.aivanovski.leetcode.android.presentation.core.compose.cells.ui.newSpaceCell
import com.aivanovski.leetcode.android.presentation.core.compose.cells.ui.newTextChipRowCell
import com.aivanovski.leetcode.android.presentation.core.compose.cells.ui.newTextFieldCell
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.ButtonCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.SecretFieldCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.SpaceCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.TextChipRowCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.TextFieldCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ElementMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.HalfMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.toTextStyle
import com.aivanovski.leetcode.android.presentation.core.mvvm.SubscribeToLifecycleEffect
import com.aivanovski.leetcode.android.presentation.login.model.LoginIntent
import com.aivanovski.leetcode.android.presentation.login.model.LoginState

@Composable
fun LoginScreen(screen: Screen) {
    val factory = remember(screen) { LoginViewModel.Factory() }
    val viewModel: LoginViewModel = viewModel(factory = factory)

    val state by viewModel.state.collectAsState()

    LoginScreenContent(
        state = state,
        onIntent = viewModel::sendIntent
    )

    SubscribeToLifecycleEffect(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreenContent(
    state: LoginState,
    onIntent: (intent: LoginIntent) -> Unit
) {
    Scaffold(
        topBar = {
            ScreenTopBar(
                title = stringResource(R.string.log_in),
                onBack = { onIntent.invoke(LoginIntent.OnBackClick) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                LoginState.Loading -> {
                    CenteredBox { CircularProgressIndicator() }
                }

                is LoginState.Data -> {
                    LoginContent(
                        state = state,
                        onIntent = onIntent
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginContent(
    state: LoginState.Data,
    onIntent: (intent: LoginIntent) -> Unit
) {
    // TODO: call if need
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ElementMargin)
    ) {
        if (state.errorMessage != null) {
            ErrorContent(
                message = state.errorMessage
            )
        }

        state.cellViewModels.forEach { cellViewModel ->
            RenderCell(cellViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenTopBar(
    title: String,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = TextSize.TITLE_LARGE.toTextStyle(),
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.primaryText,
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppTheme.colors.background
        )
    )
}

@Composable
private fun RenderCell(viewModel: CellViewModel) {
    when (viewModel) {
        is SpaceCellViewModel -> SpaceCell(viewModel)
        is ButtonCellViewModel -> ButtonCell(viewModel)
        is TextFieldCellViewModel -> TextFieldCell(viewModel)
        is SecretFieldCellViewModel -> SecretFieldCell(viewModel)
        is TextChipRowCellViewModel -> TextChipRowCell(viewModel)
        else -> throw IllegalArgumentException("Unknown cell: $viewModel")
    }
}

@Preview
@Composable
fun LoginScreenPreview_Loading() {
    ThemedScreenPreview(theme = LightTheme) {
        LoginScreenContent(
            state = LoginState.Loading,
            onIntent = {}
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview_Data() {
    ThemedScreenPreview(theme = LightTheme) {
        LoginScreenContent(
            state = newDataState(),
            onIntent = {}
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview_Error() {
    ThemedScreenPreview(theme = LightTheme) {
        LoginScreenContent(
            state = newErrorState(),
            onIntent = {}
        )
    }
}

@Composable
private fun newDataState() =
    LoginState.Data(
        cellViewModels = listOf(
            newSpaceCell(),
            newTextChipRowCell(items = listOf("debug@example.com")),
            newSpaceCell(height = HalfMargin),
            newTextFieldCell(),
            newSpaceCell(height = HalfMargin),
            newSecretFieldCell(),
            newSpaceCell()
        ),
        errorMessage = null
    )

private fun newErrorState() =
    LoginState.Data(
        cellViewModels = emptyList(),
        errorMessage = ErrorMessage(
            message = "Invalid email or password"
        )
    )