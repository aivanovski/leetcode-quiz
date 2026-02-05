package com.aivanovski.leetcode.android.presentation

import androidx.navigation3.runtime.NavEntry
import com.aivanovski.leetcode.android.presentation.problemDetails.ProblemDetailsScreen
import com.aivanovski.leetcode.android.presentation.problemList.ProblemsScreen
import com.aivanovski.leetcode.android.presentation.quiz.QuizScreen
import com.aivanovski.leetcode.android.presentation.settings.SettingsScreen

fun navigationRoutes(screen: Screen): NavEntry<Screen> {
    return when (screen) {
        is Screen.ProblemList -> NavEntry(screen) {
            ProblemsScreen()
        }

        is Screen.ProblemDetails -> NavEntry(screen) {
            ProblemDetailsScreen(screen = screen)
        }

        is Screen.Quiz -> NavEntry(screen) {
            QuizScreen(screen)
        }

        is Screen.Settings -> NavEntry(screen) {
            SettingsScreen(screen)
        }
    }
}