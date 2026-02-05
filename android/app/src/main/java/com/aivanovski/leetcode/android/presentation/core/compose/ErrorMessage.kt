package com.aivanovski.leetcode.android.presentation.core.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ElementSpace
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.preview.longText
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ElementMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme

@Composable
fun ErrorContent(
    message: ErrorMessage,
    onAction: ((actionId: Int) -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ElementMargin)
    ) {
        Text(
            text = stringResource(R.string.error),
            style = TextSize.TITLE_LARGE.toTextStyle(),
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.primaryText
        )

        Spacer(modifier = Modifier.height(ElementMargin))

        Text(
            text = message.message,
            style = TextSize.BODY_LARGE.toTextStyle(),
            color = AppTheme.colors.primaryText
        )

        if (message.actionText?.isNotEmpty() == true) {
            Spacer(modifier = Modifier.height(ElementMargin))

            Button(
                onClick = {
                    if (message.actionId != null) {
                        onAction?.invoke(message.actionId)
                    }
                }
            ) {
                Text(
                    text = message.actionText
                )
            }
        }
    }
}

@Preview
@Composable
fun ErrorContentPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        Column {
            ErrorContent(
                message = newErrorMessage(),
                onAction = {}
            )

            ElementSpace()

            ErrorContent(
                message = newErrorMessage(actionText = ""),
                onAction = {}
            )
        }
    }
}

@Composable
fun newErrorMessage(
    message: String = longText(),
    actionText: String = "Retry"
) = ErrorMessage(
    message = message,
    actionText = actionText
)