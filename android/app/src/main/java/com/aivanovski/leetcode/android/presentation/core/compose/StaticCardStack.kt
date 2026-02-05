package com.aivanovski.leetcode.android.presentation.core.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun CardStack(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content,
        modifier
    ) { measurables, constraints ->
        val placeables =
            measurables.map { measurable ->
                measurable.measure(constraints)
            }

        val height =
            if (placeables.isNotEmpty()) {
                placeables.first().height +
                    (CardStack.EXTRA_PADDING * placeables.size)
            } else {
                0
            }

        val width =
            if (placeables.isNotEmpty()) {
                placeables.first().width
            } else {
                0
            }

        layout(width = width, height = height) {
            placeables.mapIndexed { index, placeable ->
                placeable.place(
                    x = if (index % 2 == 0) {
                        0
                    } else {
                        CardStack.X_POSITION
                    },
                    y = CardStack.Y_POSITION * index
                )
            }
        }
    }
}

object CardStack {
    const val EXTRA_PADDING = 10
    const val Y_POSITION = 5
    const val X_POSITION = 5
}