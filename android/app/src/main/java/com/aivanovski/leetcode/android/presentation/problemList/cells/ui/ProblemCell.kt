package com.aivanovski.leetcode.android.presentation.problemList.cells.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.aivanovski.leetcode.android.presentation.core.compose.preview.PreviewEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.rememberOnClickedCallback
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.problemList.cells.model.ProblemCellEvent
import com.aivanovski.leetcode.android.presentation.problemList.cells.model.ProblemCellModel
import com.aivanovski.leetcode.android.presentation.problemList.cells.viewModel.ProblemCellViewModel

@Composable
fun ProblemCell(viewModel: ProblemCellViewModel) {
    val model = viewModel.model

    val onClick = rememberOnClickedCallback {
        viewModel.sendEvent(ProblemCellEvent.OnClick(model.problemId))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                DifficultyBadge(difficulty = model.difficulty)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = model.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = model.categoryTitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatItem(
                    label = "Acceptance",
                    value = model.acceptanceRate
                )
                StatItem(
                    label = "Likes",
                    value = model.likes
                )
                StatItem(
                    label = "Submissions",
                    value = model.submissions
                )
            }
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
            .background(color = color, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = difficulty,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
fun ProblemCellPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        ProblemCell(newProblemCellViewModel())
    }
}

@Composable
fun newProblemCellViewModel() =
    ProblemCellViewModel(
        model = ProblemCellModel(
            id = "1",
            problemId = 1,
            number = "#1",
            title = "Two Sum",
            categoryTitle = "Algorithms",
            difficulty = "Easy",
            likes = "42.5K",
            acceptanceRate = "49.3%",
            submissions = "10.2M"
        ),
        eventProvider = PreviewEventProvider
    )