package com.aivanovski.leetcode.android.presentation.core.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ElementMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.QuarterMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.SmallMargin

@Composable
fun TextChip(
    text: String,
    textSize: TextSize = TextSize.BODY_MEDIUM,
    textColor: Color = AppTheme.theme.colors.primaryText,
    cardColor: Color = AppTheme.theme.colors.cardPrimaryBackground,
    onClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(size = SmallMargin),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clickable(
                    onClick = onClick ?: {}
                )
                .padding(
                    horizontal = SmallMargin,
                    vertical = QuarterMargin
                )

        ) {
            Text(
                text = text,
                style = textSize.toTextStyle(),
                color = textColor
            )
        }
    }
}

@Composable
@Preview
fun ChipPreview() {
    ThemedPreview(theme = LightTheme) {
        Column(
            modifier = Modifier
                .padding(all = ElementMargin)
        ) {
            TextChip(
                text = "256 executions"
            )

            Spacer(Modifier.height(SmallMargin))

            TextChip(
                text = "RUNNING",
                textColor = AppTheme.theme.colors.primaryText,
                cardColor = AppTheme.theme.colors.cardPrimaryBackground
            )

            Spacer(Modifier.height(SmallMargin))

            TextChip(
                text = "STOPPED",
                textColor = AppTheme.theme.colors.primaryText,
                textSize = TextSize.TITLE_MEDIUM,
                cardColor = AppTheme.theme.colors.cardPrimaryBackground
            )
        }
    }
}