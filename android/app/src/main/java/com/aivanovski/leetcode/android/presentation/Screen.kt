package com.aivanovski.leetcode.android.presentation

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsArgs

@Immutable
sealed class Screen {

    @Immutable
    data object Quiz : Screen()

    @Immutable
    data object ProblemList : Screen()

    @Immutable
    data object Settings : Screen()

    @Immutable
    data class ProblemDetails(
        val args: ProblemDetailsArgs
    ) : Screen()
}