package com.aivanovski.leetcode.android.presentation.problemList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.presentation.core.compose.CenteredBox
import com.aivanovski.leetcode.android.presentation.core.compose.ErrorContent
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.ui.SpaceCell
import com.aivanovski.leetcode.android.presentation.core.compose.cells.ui.newSpaceCell
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.SpaceCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.toTextStyle
import com.aivanovski.leetcode.android.presentation.core.mvvm.SubscribeToLifecycleEffect
import com.aivanovski.leetcode.android.presentation.problemList.cells.ui.ProblemCell
import com.aivanovski.leetcode.android.presentation.problemList.cells.ui.newProblemCell
import com.aivanovski.leetcode.android.presentation.problemList.cells.viewModel.ProblemCellViewModel
import com.aivanovski.leetcode.android.presentation.problemList.model.ProblemListIntent
import com.aivanovski.leetcode.android.presentation.problemList.model.ProblemListState

@Composable
fun ProblemListScreen() {
    val factory = remember { ProblemListViewModel.Factory() }
    val viewModel: ProblemListViewModel = viewModel(factory = factory)

    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()

    ProblemListScreen(
        state = state,
        isRefreshing = isRefreshing,
        searchQuery = searchQuery,
        isSearchActive = isSearchActive,
        onSearchQueryChange = viewModel::onSearchQueryChanged,
        onCloseSearch = viewModel::onCloseSearch,
        onSearchClicked = viewModel::onSearchClicked,
        onErrorAction = viewModel::onErrorAction,
        onIntent = viewModel::sendIntent
    )

    SubscribeToLifecycleEffect(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProblemListScreen(
    state: ProblemListState,
    isRefreshing: Boolean,
    searchQuery: String,
    isSearchActive: Boolean,
    onSearchQueryChange: (query: String) -> Unit,
    onCloseSearch: () -> Unit,
    onSearchClicked: () -> Unit,
    onErrorAction: (actionId: Int) -> Unit,
    onIntent: (intent: ProblemListIntent) -> Unit
) {
    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchTopBar(
                    searchQuery = searchQuery,
                    onSearchQueryChanged = onSearchQueryChange,
                    onCloseSearch = onCloseSearch
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.problems),
                            style = TextSize.TITLE_LARGE.toTextStyle(),
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.primaryText
                        )
                    },
                    actions = {
                        IconButton(onClick = onSearchClicked) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = AppTheme.colors.background
                    )
                )
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                onIntent.invoke(ProblemListIntent.Refresh)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when (state) {
                is ProblemListState.Loading -> {
                    CenteredBox {
                        CircularProgressIndicator()
                    }
                }

                is ProblemListState.Error -> {
                    CenteredBox {
                        ErrorContent(
                            message = state.message,
                            onAction = onErrorAction
                        )
                    }
                }

                is ProblemListState.Data -> {
                    DataContent(
                        state = state,
                        onIntent = onIntent
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TopAppBar(
        title = {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_problems),
                        style = TextSize.TITLE_LARGE.toTextStyle()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { keyboardController?.hide() }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextSize.BODY_LARGE.toTextStyle()
            )
        },
        navigationIcon = {
            IconButton(onClick = onCloseSearch) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppTheme.colors.background
        )
    )
}

@Composable
private fun DataContent(
    state: ProblemListState.Data,
    onIntent: (intent: ProblemListIntent) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = state.cellViewModels) { viewModel ->
            RenderCell(viewModel)
        }
    }
}

@Composable
private fun RenderCell(viewModel: CellViewModel) {
    when (viewModel) {
        is ProblemCellViewModel -> ProblemCell(viewModel)
        is SpaceCellViewModel -> SpaceCell(viewModel)
    }
}

@Preview
@Composable
fun ProblemListScreen_Data() {
    ThemedScreenPreview(LightTheme) {
        ProblemListScreen(
            state = newDataState(),
            isRefreshing = false,
            searchQuery = "",
            isSearchActive = false,
            onSearchClicked = {},
            onCloseSearch = {},
            onSearchQueryChange = {},
            onErrorAction = {},
            onIntent = {}
        )
    }
}

@Composable
private fun newDataState() =
    ProblemListState.Data(
        cellViewModels = listOf(
            newProblemCell(),
            newSpaceCell(),
            newProblemCell()
        )
    )