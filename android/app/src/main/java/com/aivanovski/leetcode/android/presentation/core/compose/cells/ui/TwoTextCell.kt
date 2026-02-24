package com.aivanovski.leetcode.android.presentation.core.compose.cells.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.aivanovski.leetcode.android.presentation.core.compose.TextColor
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TwoTextCellEvent
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TwoTextCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.TwoTextCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ElementSpace
import com.aivanovski.leetcode.android.presentation.core.compose.preview.PreviewEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedPreview
import com.aivanovski.leetcode.android.presentation.core.compose.preview.longText
import com.aivanovski.leetcode.android.presentation.core.compose.preview.shortText
import com.aivanovski.leetcode.android.presentation.core.compose.rememberOnClickedCallback
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ElementMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.QuarterMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.TwoLineItemHeight
import com.aivanovski.leetcode.android.presentation.core.compose.toColor
import com.aivanovski.leetcode.android.presentation.core.compose.toTextStyle

@Composable
fun TwoTextCell(viewModel: TwoTextCellViewModel) {
    val model = viewModel.model

    val onClick = rememberOnClickedCallback {
        viewModel.sendEvent(TwoTextCellEvent.OnClick(model.id))
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = TwoLineItemHeight)
            .clickable(onClick = onClick)
            .padding(
                horizontal = ElementMargin,
                vertical = QuarterMargin
            )
    ) {
        Text(
            text = model.primaryText,
            color = model.primaryTextColor.toColor(),
            style = model.primaryTextSize.toTextStyle()
        )
        Text(
            text = model.secondaryText,
            color = model.secondaryTextColor.toColor(),
            style = model.secondaryTextSize.toTextStyle()
        )
    }
}

@Composable
@Preview
fun TwoTextCellPreview() {
    ThemedPreview(
        theme = LightTheme
    ) {
        Column {
            ElementSpace()
            TwoTextCell(newTwoTextCell())
            ElementSpace()
            TwoTextCell(
                newTwoTextCell(
                    primaryText = "Status",
                    secondaryText = "Something went wrong",
                    secondaryTextColor = TextColor.ERROR
                )
            )
            ElementSpace()
            TwoTextCell(
                newTwoTextCell(
                    primaryText = longText(),
                    secondaryText = longText()
                )
            )
        }
    }
}

@Composable
fun newTwoTextCell(
    primaryText: String = shortText(),
    secondaryText: String = shortText(),
    primaryTextSize: TextSize = TextSize.TITLE_MEDIUM,
    secondaryTextSize: TextSize = TextSize.BODY_MEDIUM,
    primaryTextColor: TextColor = TextColor.PRIMARY,
    secondaryTextColor: TextColor = TextColor.SECONDARY
) = TwoTextCellViewModel(
    model = TwoTextCellModel(
        id = "id",
        primaryText = primaryText,
        secondaryText = secondaryText,
        primaryTextSize = primaryTextSize,
        secondaryTextSize = secondaryTextSize,
        primaryTextColor = primaryTextColor,
        secondaryTextColor = secondaryTextColor
    ),
    eventProvider = PreviewEventProvider
)