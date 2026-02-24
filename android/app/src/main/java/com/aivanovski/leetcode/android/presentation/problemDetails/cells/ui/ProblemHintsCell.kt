package com.aivanovski.leetcode.android.presentation.problemDetails.cells.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.presentation.core.compose.CornersShape
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.HalfMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.toComposeShape
import com.aivanovski.leetcode.android.presentation.core.compose.toTextStyle
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.model.ProblemHintsCellModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel.ProblemHintsCellViewModel

@Composable
fun ProblemHintsCell(viewModel: ProblemHintsCellViewModel) {
    val model = viewModel.model

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
            Text(
                text = stringResource(R.string.hints),
                style = TextSize.TITLE_LARGE.toTextStyle(),
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.primaryText
            )

            Spacer(modifier = Modifier.height(12.dp))

            model.hints.forEachIndexed { index, hint ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "${index + 1}. ",
                        style = TextSize.BODY_MEDIUM.toTextStyle(),
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.primaryText
                    )
                    Text(
                        text = hint,
                        style = TextSize.BODY_MEDIUM.toTextStyle(),
                        color = AppTheme.colors.primaryText
                    )
                }
                if (index < model.hints.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun ProblemHintsCellPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        ProblemHintsCell(newProblemHintsCellViewModel())
    }
}

@Composable
fun newProblemHintsCellViewModel() =
    ProblemHintsCellViewModel(
        model = ProblemHintsCellModel(
            id = "hints-1",
            hints = listOf(
                "A really brute force way would be to search for all possible pairs of numbers but that would be too slow.",
                "Think about using a hash map to solve this problem."
            )
        )
    )