package com.aivanovski.leetcode.android.presentation.quiz.cells.ui

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.aivanovski.leetcode.android.presentation.core.compose.CornersShape
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.preview.PreviewEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.preview.longText
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ElementMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.toComposeShape
import com.aivanovski.leetcode.android.presentation.core.compose.toTextStyle
import com.aivanovski.leetcode.android.presentation.quiz.cells.model.QuestionCardCellModel
import com.aivanovski.leetcode.android.presentation.quiz.cells.viewModel.QuestionCardCellViewModel

@Composable
fun QuestionCardCell(viewModel: QuestionCardCellViewModel) {
    val model = viewModel.model

    Card(
        shape = CornersShape.ALL.toComposeShape(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.theme.colors.cardPrimaryBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val (number, title, description) = createRefs()

            Text(
                text = model.number,
                style = TextSize.TITLE_MEDIUM.toTextStyle(),
                color = AppTheme.colors.primaryText,
                modifier = Modifier.constrainAs(number) {
                    top.linkTo(parent.top, margin = ElementMargin)
                    start.linkTo(parent.start, margin = ElementMargin)
                }
            )

            Text(
                text = model.title,
                textAlign = TextAlign.Center,
                style = TextSize.TITLE_LARGE.toTextStyle(),
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.primaryText,
                modifier = Modifier
                    .constrainAs(title) {
                        top.linkTo(number.bottom, margin = ElementMargin)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.matchParent
                    }
                    .padding(horizontal = ElementMargin)
            )

            if (LocalInspectionMode.current) {
                Text(
                    text = "HTML CONTENT",
                    textAlign = TextAlign.Center,
                    color = AppTheme.colors.primaryText,
                    modifier = Modifier
                        .constrainAs(description) {
                            top.linkTo(title.bottom, margin = ElementMargin)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.matchParent
                        }
                        .padding(horizontal = ElementMargin)
                )
            } else {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: android.webkit.WebResourceRequest?
                                ): Boolean {
                                    return true // Block all link navigation
                                }
                            }
                            settings.apply {
                                javaScriptEnabled = false
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                isVerticalScrollBarEnabled = false
                            }
                            setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        }
                    },
                    update = { webView ->
                        webView.loadDataWithBaseURL(
                            null,
                            model.frontHtmlContent,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    },
                    modifier = Modifier
                        .constrainAs(description) {
                            top.linkTo(title.bottom, margin = ElementMargin)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.matchParent
                        }
                        .padding(horizontal = ElementMargin)
                )
            }
        }
    }
}

@Preview
@Composable
fun QuestionCardCellPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        QuestionCardCell(newQuestionCardCell())
    }
}

@Composable
fun newQuestionCardCell() =
    QuestionCardCellViewModel(
        model = QuestionCardCellModel(
            id = "",
            number = "#0001",
            title = "Two Sum",
            frontHtmlContent = longText(),
            backContent = longText()
        ),
        eventProvider = PreviewEventProvider
    )