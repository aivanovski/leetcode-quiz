package com.aivanovski.leetcode.android.presentation.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.icons.VectorIcon
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.DialogCardCornerSize
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ElementMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.OneLineMediumItemHeight
import com.aivanovski.leetcode.android.presentation.core.compose.toTextStyle
import com.aivanovski.leetcode.android.presentation.quiz.model.HintDialogState

@Composable
fun HintDialog(
    state: HintDialogState,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(DialogCardCornerSize)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                for ((index, hint) in state.hints.withIndex()) {
                    CollapsibleItem(
                        title = "Hint ${index + 1}",
                        content = hint
                    )
                }

                CollapsibleItem(
                    title = "Algorithm",
                    content = state.algorithmHint
                )

                for ((index, solution) in state.solutions.withIndex()) {
                    CollapsibleItem(
                        title = "Solution ${index + 1}",
                        content = solution
                    )
                }
            }
        }

    }
}

@Composable
fun CollapsibleItem(
    title: String,
    content: String
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(height = OneLineMediumItemHeight)
                .clickable(
                    onClick = {
                        isExpanded = !isExpanded
                    }
                )
                .padding(horizontal = ElementMargin)
        ) {
            Text(
                text = title,
                style = TextSize.BODY_MEDIUM.toTextStyle(),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            val icon = if (isExpanded) {
                VectorIcon.COLLAPSE_UP.vector
            } else {
                VectorIcon.COLLAPSE_DOWN.vector
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppTheme.theme.colors.primaryText
            )
        }

        if (isExpanded) {
            Text(
                text = content,
                style = TextSize.BODY_SMALL.toTextStyle(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            isExpanded = !isExpanded
                        }
                    )
                    .padding(start = ElementMargin)
            )
        }
    }
}

@Preview
@Composable
fun HintDialogPreview() {
    ThemedScreenPreview(LightTheme) {
        HintDialog(
            state = newHintDialogState(),
            onDismiss = {}
        )
    }
}

private fun newHintDialogState() =
    HintDialogState(
        hints = listOf("Some hint", "Another hint"),
        algorithmHint = "",
        formula = "dp[i]=max(dp[i-1],nums[i])",
        solutions = listOf(
            "First",
            "Second"
        )
    )