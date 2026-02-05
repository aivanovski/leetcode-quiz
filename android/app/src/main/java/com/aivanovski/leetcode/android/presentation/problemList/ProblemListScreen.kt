package com.aivanovski.leetcode.android.presentation.problemList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aivanovski.leetcode.android.presentation.core.compose.CenteredBox
import com.aivanovski.leetcode.android.presentation.core.compose.ErrorContent
import com.aivanovski.leetcode.android.presentation.problemList.cells.ui.ProblemCell
import com.aivanovski.leetcode.android.presentation.problemList.cells.viewModel.ProblemCellViewModel
import com.aivanovski.leetcode.android.presentation.problemList.model.QuestionsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemsScreen() {
    val factory = remember { ProblemListViewModel.Factory() }
    val viewModel: ProblemListViewModel = viewModel(factory = factory)

    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchTopBar(
                    searchQuery = searchQuery,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                    onCloseSearch = viewModel::onCloseSearch
                )
            } else {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "LeetCode Problems",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        IconButton(onClick = viewModel::onSearchClicked) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when (val currentState = state) {
                is QuestionsState.Loading -> {
                    CenteredBox {
                        CircularProgressIndicator()
                    }
                }

                is QuestionsState.Error -> {
                    CenteredBox {
                        ErrorContent(
                            message = currentState.message,
                            onAction = viewModel::onErrorAction
                        )
                    }
                }

                is QuestionsState.Data -> {
                    DataContent(
                        cellViewModels = currentState.cellViewModels
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
                        text = "Search problems...",
                        style = MaterialTheme.typography.bodyLarge
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
                textStyle = MaterialTheme.typography.bodyLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onCloseSearch) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close search"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DataContent(cellViewModels: List<ProblemCellViewModel>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(
            items = cellViewModels,
            key = { it.model.id }
        ) { viewModel ->
            ProblemCell(viewModel)
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}