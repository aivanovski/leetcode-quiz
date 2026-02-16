package com.aivanovski.leetcode.android.presentation.quizStart

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.core.compose.CenteredBox
import com.aivanovski.leetcode.android.presentation.core.compose.CenteredColumn
import com.aivanovski.leetcode.android.presentation.core.compose.ErrorContent
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.preview.Space
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ElementMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.toTextStyle
import com.aivanovski.leetcode.android.presentation.core.mvvm.SubscribeToLifecycleEffect
import com.aivanovski.leetcode.android.presentation.quizStart.model.QuizStartIntent
import com.aivanovski.leetcode.android.presentation.quizStart.model.QuizStartIntent.OnErrorAction
import com.aivanovski.leetcode.android.presentation.quizStart.model.QuizStartIntent.OnRefresh
import com.aivanovski.leetcode.android.presentation.quizStart.model.QuizStartIntent.OnStartClick
import com.aivanovski.leetcode.android.presentation.quizStart.model.QuizStartState

@Composable
fun QuizStartScreen(screen: Screen.QuizStart) {
    val factory = remember(screen) { QuizStartViewModel.Factory() }
    val viewModel: QuizStartViewModel = viewModel(factory = factory)

    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    QuizStartScreen(
        state = state,
        onIntent = viewModel::sendIntent,
        isRefreshing = isRefreshing
    )

    SubscribeToLifecycleEffect(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizStartScreen(
    state: QuizStartState,
    onIntent: (intent: QuizStartIntent) -> Unit,
    isRefreshing: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.quiz),
                        style = TextSize.TITLE_LARGE.toTextStyle(),
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.primaryText
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppTheme.colors.background
                )
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { onIntent.invoke(OnRefresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when (state) {
                is QuizStartState.Loading -> {
                    CenteredBox {
                        CircularProgressIndicator()
                    }
                }

                is QuizStartState.Error -> {
                    CenteredBox {
                        ErrorContent(
                            message = state.message,
                            onAction = { actionId ->
                                onIntent.invoke(OnErrorAction(actionId))
                            }
                        )
                    }
                }

                is QuizStartState.Data -> {
                    DataContent(
                        state = state,
                        onStartClick = { onIntent.invoke(OnStartClick) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DataContent(
    state: QuizStartState.Data,
    onStartClick: () -> Unit
) {
    CenteredColumn(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = state.title,
            style = TextSize.TITLE_LARGE.toTextStyle(),
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.primaryText
        )

        Space(ElementMargin)

        Button(
            onClick = onStartClick
        ) {
            Text(
                text = state.buttonText
            )
        }
    }
}

@Preview
@Composable
fun QuizStartScreen_DataPreview() {
    ThemedScreenPreview(LightTheme) {
        QuizStartScreen(
            state = newDataState(),
            onIntent = {},
            isRefreshing = false
        )
    }
}

private fun newDataState() =
    QuizStartState.Data(
        title = "16/48 Questions",
        buttonText = "Continue"
    )