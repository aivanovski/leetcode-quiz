package com.aivanovski.leetcode.android.domain

import com.aivanovski.leetcode.android.presentation.core.compose.theme.ThemeProvider
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider
import com.aivanovski.leetcode.android.utils.toHexFormat

class ProblemHtmlFormatter(
    private val resourceProvider: ResourceProvider,
    private val themeProvider: ThemeProvider
) {

    fun formatProblemHtml(content: String): String {
        if (content.isBlank()) return content

        val textColor = themeProvider.theme.colors.primaryText
        val codeBackgroundColor = themeProvider.theme.colors.secondaryBackground
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    font-size: 16px;
                    line-height: 1.6;
                    color: 0x${textColor.toHexFormat()};
                    margin: 0;
                    padding: 0;
                }
                a {
                    color: 0x${textColor.toHexFormat()};
                }
                pre, code {
                    background-color: 0x${codeBackgroundColor.toHexFormat()};
                    border-radius: 4px;
                    padding: 2px 6px;
                    font-family: 'Courier New', Courier, monospace;
                    font-size: 14px;
                }
                pre {
                    padding: 12px;
                    overflow-x: auto;
                }
                pre code {
                    padding: 0;
                    background-color: transparent;
                }
                img {
                    max-width: 100%;
                    height: auto;
                }
                ul, ol {
                    padding-left: 20px;
                }
            </style>
        </head>
        <body>
            $content
        </body>
        </html>
        """.trimIndent()
    }
}