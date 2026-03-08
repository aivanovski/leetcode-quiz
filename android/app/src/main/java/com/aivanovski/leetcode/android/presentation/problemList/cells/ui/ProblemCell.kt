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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.preview.PreviewEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.CardCornerSize
import com.aivanovski.leetcode.android.presentation.core.compose.theme.CardElevation
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ElementMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.toTextStyle
import com.aivanovski.leetcode.android.presentation.problemList.cells.model.ProblemCellEvent
import com.aivanovski.leetcode.android.presentation.problemList.cells.model.ProblemCellModel
import com.aivanovski.leetcode.android.presentation.problemList.cells.viewModel.ProblemCellViewModel

@Composable
fun ProblemCell(viewModel: ProblemCellViewModel) {
    val model = viewModel.model

    Card(
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.theme.colors.cardPrimaryBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
        shape = RoundedCornerShape(CardCornerSize),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        viewModel.sendEvent(ProblemCellEvent.OnClick(model.problemId))
                    }
                )
                .padding(ElementMargin)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = model.number,
                    style = TextSize.TITLE_MEDIUM.toTextStyle(),
                    color = AppTheme.colors.primaryText
                )
                DifficultyBadge(difficulty = model.difficulty)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = model.title,
                style = TextSize.TITLE_LARGE.toTextStyle(),
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.primaryText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = model.categoryTitle,
                style = TextSize.BODY_SMALL.toTextStyle(),
                color = AppTheme.colors.secondaryText
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
    val color = when (difficulty.lowercase()) {
        "easy" -> Color(0xFF10B981)
        "medium" -> Color(0xFFF59E0B)
        "hard" -> Color(0xFFEF4444)
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .background(color = color, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = difficulty,
            style = TextSize.BODY_SMALL.toTextStyle(),
            fontWeight = FontWeight.Medium,
            color = AppTheme.colors.textOnSecondary
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
            style = TextSize.BODY_SMALL.toTextStyle(),
            color = AppTheme.colors.secondaryText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = TextSize.BODY_MEDIUM.toTextStyle(),
            fontWeight = FontWeight.Medium,
            color = AppTheme.colors.primaryText
        )
    }
}

@Preview
@Composable
fun ProblemCellPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        ProblemCell(newProblemCell())
    }
}

@Composable
fun newProblemCell() =
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