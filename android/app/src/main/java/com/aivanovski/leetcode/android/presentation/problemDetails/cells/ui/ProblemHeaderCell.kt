package com.aivanovski.leetcode.android.presentation.problemDetails.cells.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.model.ProblemHeaderCellModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel.ProblemHeaderCellViewModel

@Composable
fun ProblemHeaderCell(viewModel: ProblemHeaderCellViewModel) {
    val model = viewModel.model

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = model.number,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                DifficultyBadge(difficulty = model.difficulty)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = model.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = model.categoryTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: String) {
    val (color, textColor) = when (difficulty.lowercase()) {
        "easy" -> Color(0xFF10B981) to Color.White
        "medium" -> Color(0xFFF59E0B) to Color.White
        "hard" -> Color(0xFFEF4444) to Color.White
        else ->
            MaterialTheme.colorScheme.surfaceVariant to
                MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .background(color = color, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = difficulty,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Preview
@Composable
fun ProblemHeaderCellPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        ProblemHeaderCell(newProblemHeaderCell())
    }
}

@Composable
fun newProblemHeaderCell() =
    ProblemHeaderCellViewModel(
        model = ProblemHeaderCellModel(
            id = "header-1",
            problemId = 1,
            number = "#1",
            title = "Two Sum",
            categoryTitle = "Algorithms",
            difficulty = "Easy"
        )
    )