package com.aivanovski.leetcode.android.presentation

import androidx.navigation3.runtime.NavEntry
import com.aivanovski.leetcode.android.presentation.login.LoginScreen
import com.aivanovski.leetcode.android.presentation.problemDetails.ProblemDetailsScreen
import com.aivanovski.leetcode.android.presentation.problemList.ProblemListScreen
import com.aivanovski.leetcode.android.presentation.quiz.QuizScreen
import com.aivanovski.leetcode.android.presentation.quizStart.QuizStartScreen
import com.aivanovski.leetcode.android.presentation.settings.SettingsScreen

fun navigationRoutes(screen: Screen): NavEntry<Screen> {
    return when (screen) {
        is Screen.Login -> NavEntry(screen) {
            LoginScreen(screen)
        }

        is Screen.ProblemList -> NavEntry(screen) {
            ProblemListScreen()
        }

        is Screen.ProblemDetails -> NavEntry(screen) {
            ProblemDetailsScreen(screen = screen)
        }

        is Screen.QuizStart -> NavEntry(screen) {
            QuizStartScreen(screen)
        }

        is Screen.Quiz -> NavEntry(screen) {
            QuizScreen(screen)
        }

        is Screen.Settings -> NavEntry(screen) {
            SettingsScreen(screen)
        }
    }
}