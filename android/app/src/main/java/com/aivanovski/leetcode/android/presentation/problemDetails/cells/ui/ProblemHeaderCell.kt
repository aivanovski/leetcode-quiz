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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aivanovski.leetcode.android.presentation.core.compose.CornersShape
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.HalfMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.toComposeShape
import com.aivanovski.leetcode.android.presentation.core.compose.toTextStyle
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.model.ProblemHeaderCellModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel.ProblemHeaderCellViewModel

@Composable
fun ProblemHeaderCell(viewModel: ProblemHeaderCellViewModel) {
    val model = viewModel.model

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = CornersShape.ALL.toComposeShape(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.theme.colors.cardPrimaryBackground
        ),
        modifier = Modifier.padding(horizontal = HalfMargin)
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
                    style = TextSize.TITLE_MEDIUM.toTextStyle(),
                    color = AppTheme.colors.primaryText,
                    fontWeight = FontWeight.Bold
                )
                DifficultyBadge(difficulty = model.difficulty)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = model.title,
                style = TextSize.TITLE_LARGE.toTextStyle(),
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.primaryText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = model.categoryTitle,
                style = TextSize.BODY_MEDIUM.toTextStyle(),
                color = AppTheme.colors.primaryText
            )
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: String) {
    val color = when (difficulty.lowercase()) {
        "easy" -> Color(0xFF10B981)
        "medium" -> Color(0xFFF59E0B)
        "hard" -> Color(0xFFEF4444)
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .background(
                color = color,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
    ) {
        Text(
            text = difficulty,
            style = TextSize.BODY_MEDIUM.toTextStyle(),
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.textOnSecondary
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