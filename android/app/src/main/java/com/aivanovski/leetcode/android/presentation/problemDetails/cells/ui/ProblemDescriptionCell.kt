package com.aivanovski.leetcode.android.presentation.problemDetails.cells.ui

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.aivanovski.leetcode.android.presentation.core.compose.CenteredBox
import com.aivanovski.leetcode.android.presentation.core.compose.CornersShape
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedScreenPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.AppTheme
import com.aivanovski.leetcode.android.presentation.core.compose.theme.HalfMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import com.aivanovski.leetcode.android.presentation.core.compose.toComposeShape
import com.aivanovski.leetcode.android.presentation.core.compose.toTextStyle
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.model.ProblemDescriptionCellModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel.ProblemDescriptionCellViewModel

@Composable
fun ProblemDescriptionCell(viewModel: ProblemDescriptionCellViewModel) {
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
                text = "Description",
                style = TextSize.TITLE_LARGE.toTextStyle(),
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.primaryText
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (LocalInspectionMode.current) {
                CenteredBox {
                    Text(
                        text = "HTML CONTENT",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                HtmlContent(
                    html = model.htmlContent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun HtmlContent(
    html: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
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
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }
    )
}

@Preview
@Composable
fun ProblemDescriptionCellPreview() {
    ThemedScreenPreview(theme = LightTheme) {
        ProblemDescriptionCell(newProblemDescriptionCell())
    }
}

@Composable
fun newProblemDescriptionCell() =
    ProblemDescriptionCellViewModel(
        model = ProblemDescriptionCellModel(
            id = "description-1",
            htmlContent = ""
        )
    )