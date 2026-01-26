package com.aivanovski.leetcode.android.presentation.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.core.compose.CenteredBox
import com.aivanovski.leetcode.android.presentation.core.compose.CenteredColumn
import com.aivanovski.leetcode.android.presentation.core.compose.ErrorContent
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.preview.Space
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.rememberOnClickedCallback
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ElementMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.HalfMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.toTextStyle
import com.aivanovski.leetcode.android.presentation.quiz.cells.ui.QuestionCardCell
import com.aivanovski.leetcode.android.presentation.quiz.cells.ui.newQuestionCardCell
import com.aivanovski.leetcode.android.presentation.quiz.model.Answer
import com.aivanovski.leetcode.android.presentation.quiz.model.QuizState

@Composable
fun QuizScreen(screen: Screen) {
    val factory = remember(screen) { QuizViewModel.Factory() }
    val viewModel: QuizViewModel = viewModel(factory = factory)

    val state by viewModel.state.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    QuizScreenContent(
        state = state,
        isRefreshing = isRefreshing,
        onRefresh = viewModel::onRefresh,
        onAnswer = viewModel::onCardAnswered,
        onErrorAction = viewModel::onErrorAction,
        onRestart = viewModel::onRestartClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreenContent(
    state: QuizState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onAnswer: (answer: Answer) -> Unit,
    onErrorAction: (actionId: Int) -> Unit,
    onRestart: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Quiz",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    is QuizState.Loading -> {
                        CenteredBox { CircularProgressIndicator() }
                    }

                    is QuizState.Error -> {
                        CenteredBox {
                            ErrorContent(
                                message = state.error,
                                onAction = onErrorAction
                            )
                        }
                    }

                    is QuizState.Card -> {
                        CardContent(
                            state = state,
                            onAnswer = onAnswer
                        )
                    }

                    is QuizState.Result -> {
                        ResultContent(
                            state = state,
                            onRestart = onRestart
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultContent(
    state: QuizState.Result,
    onRestart: () -> Unit
) {
    CenteredBox {
        CenteredColumn {
            Text(
                text = stringResource(R.string.questionnaire_stats_title),
                style = TextSize.TITLE_LARGE.toTextStyle(),
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.primaryText
            )

            Space(ElementMargin)

            Text(
                text = stringResource(
                    R.string.questionnaire_stats_message,
                    state.positivelyAnswered,
                    state.negativelyAnswered,
                    state.questionsAnswered
                ),
                style = TextSize.BODY_LARGE.toTextStyle(),
                textAlign = TextAlign.Center
            )

            Space(ElementMargin)

            Button(
                onClick = onRestart
            ) {
                Text(
                    text = stringResource(R.string.next_round)
                )
            }
        }
    }
}

@Composable
private fun CardContent(
    state: QuizState.Card,
    onAnswer: (answer: Answer) -> Unit
) {
    val badButtonClick = rememberOnClickedCallback {
        onAnswer.invoke(Answer.BAD)
    }
    val goodButtonClick = rememberOnClickedCallback {
        onAnswer.invoke(Answer.GOOD)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(HalfMargin)
        ) {
            Text(
                text = "${state.questionNumber} / ${state.totalQuestions}",
                style = TextSize.TITLE_LARGE.toTextStyle(),
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .padding(
                    horizontal = ElementMargin
                )
                .fillMaxWidth()
                .weight(weight = 1f)
        ) {
            QuestionCardCell(state.cardViewModel)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = ElementMargin)

        ) {
            Button(
                onClick = badButtonClick,
                modifier = Modifier.width(120.dp)
            ) {
                Text(stringResource(R.string.bad))
            }

            Box(modifier = Modifier.weight(weight = 1f))

            Button(
                onClick = goodButtonClick,
                modifier = Modifier.width(120.dp)
            ) {
                Text(stringResource(R.string.good))
            }
        }
    }
}

@Preview
@Composable
fun QuizScreenPreview_Card() {
    ThemedScreenPreview(theme = LightTheme) {
        QuizScreenContent(
            state = newCardState(),
            isRefreshing = false,
            onRefresh = {},
            onAnswer = {},
            onErrorAction = {},
            onRestart = {}
        )
    }
}

@Preview
@Composable
fun QuizScreenPreview_Result() {
    ThemedScreenPreview(theme = LightTheme) {
        QuizScreenContent(
            state = newResultState(),
            isRefreshing = false,
            onRefresh = {},
            onAnswer = {},
            onErrorAction = {},
            onRestart = {}
        )
    }
}

@Preview
@Composable
fun QuizScreenPreview_Error() {
    ThemedScreenPreview(theme = LightTheme) {
        QuizScreenContent(
            state = newErrorState(),
            isRefreshing = false,
            onRefresh = {},
            onAnswer = {},
            onErrorAction = {},
            onRestart = {}
        )
    }
}

@Composable
private fun newErrorState() =
    QuizState.Error(
        error = ErrorMessage(
            message = stringResource(R.string.network_error_message),
            actionText = stringResource(R.string.retry)
        )
    )

@Composable
private fun newCardState() =
    QuizState.Card(
        cardViewModel = newQuestionCardCell(),
        questionNumber = 8,
        totalQuestions = 15
    )

private fun newResultState() =
    QuizState.Result(
        questionsAnswered = 10,
        positivelyAnswered = 2,
        negativelyAnswered = 8
    )