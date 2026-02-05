package com.aivanovski.leetcode.android.presentation.problemDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aivanovski.leetcode.android.presentation.Screen
import com.aivanovski.leetcode.android.presentation.core.compose.CenteredBox
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.ui.ProblemDescriptionCell
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.ui.ProblemHeaderCell
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.ui.ProblemHintsCell
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel.ProblemDescriptionCellViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel.ProblemHeaderCellViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel.ProblemHintsCellViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsState

@Composable
fun ProblemDetailsScreen(screen: Screen.ProblemDetails) {
    val factory = remember(screen.args) { ProblemDetailsViewModel.Factory(screen.args) }
    val viewModel: ProblemDetailsViewModel = viewModel(
        factory = factory,
        key = screen.args.problemId.toString()
    )

    val state by viewModel.state.collectAsState()

    ProblemDetailsScreenContent(
        state = state,
        onReload = viewModel::loadData,
        onBack = viewModel::navigateBack
    )
}

@Composable
fun ProblemDetailsScreenContent(
    state: ProblemDetailsState,
    onReload: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            QuestionDetailsTopBar(onBack = onBack)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
        ) {
            when (state) {
                is ProblemDetailsState.Loading -> {
                    CenteredBox { LoadingContent() }
                }

                is ProblemDetailsState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = onReload
                    )
                }

                is ProblemDetailsState.Data -> {
                    DataContent(
                        cellViewModels = state.cellViewModels
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionDetailsTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("Question Details") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppTheme.materialColors.primaryContainer,
            titleContentColor = AppTheme.materialColors.onPrimaryContainer
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
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun DataContent(cellViewModels: List<CellViewModel>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(0.dp))
        }

        items(
            items = cellViewModels,
            key = { it.model.id }
        ) { viewModel ->
            RenderCell(viewModel)
        }

        item {
            Spacer(modifier = Modifier.height(0.dp))
        }
    }
}

@Composable
private fun RenderCell(viewModel: CellViewModel) {
    when (viewModel) {
        is ProblemHeaderCellViewModel -> ProblemHeaderCell(viewModel)
        is ProblemDescriptionCellViewModel -> ProblemDescriptionCell(viewModel)
        is ProblemHintsCellViewModel -> ProblemHintsCell(viewModel)
    }
}