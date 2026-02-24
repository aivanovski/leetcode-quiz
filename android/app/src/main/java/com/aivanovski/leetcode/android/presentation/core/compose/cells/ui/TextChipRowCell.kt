package com.aivanovski.leetcode.android.presentation.core.compose.cells.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.aivanovski.leetcode.android.presentation.core.compose.CornersShape
import com.aivanovski.leetcode.android.presentation.core.compose.TextChip
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextChipItem
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextChipRowCellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextChipRowCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.TextChipRowCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.preview.PreviewEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ElementMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.SmallMargin

@Composable
fun TextChipRowCell(viewModel: TextChipRowCellViewModel) {
    val model = viewModel.model

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = ElementMargin,
                end = ElementMargin,
                bottom = SmallMargin
            )
    ) {
        val chipCount = model.chips.size

        for (chipIndex in 0 until chipCount) {
            val chip = model.chips[chipIndex]
            if (chipIndex > 0) {
                Spacer(modifier = Modifier.width(SmallMargin))
            }

            val cardColor = if (chip.isSelected) {
                AppTheme.theme.colors.cardPrimarySelectedBackground
            } else {
                AppTheme.theme.colors.cardPrimaryBackground
            }

            TextChip(
                text = chip.text,
                textColor = chip.textColor,
                textSize = chip.textSize,
                cardColor = cardColor,
                onClick = if (chip.isClickable) {
                    { viewModel.sendIntent(TextChipRowCellEvent.OnClick(chipIndex = chipIndex)) }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
@Preview
fun TextChipRowCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        TextChipRowCell(newTextChipRowCell())
    }
}

@Composable
fun newTextChipRowCell(
    items: List<String> = listOf("1.8.0", "1.7.0"),
    shape: CornersShape = CornersShape.ALL
) = TextChipRowCellViewModel(
    model = TextChipRowCellModel(
        id = "id",
        chips = items.mapIndexed { index, item ->
            TextChipItem(
                text = item,
                textColor = AppTheme.theme.colors.primaryText,
                textSize = TextSize.TITLE_MEDIUM,
                isClickable = false,
                isSelected = (index == 0)
            )
        }
    ),
    eventProvider = PreviewEventProvider
)