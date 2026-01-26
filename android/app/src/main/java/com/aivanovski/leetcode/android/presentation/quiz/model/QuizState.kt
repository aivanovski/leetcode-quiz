package com.aivanovski.leetcode.android.presentation.quiz.model

import androidx.compose.runtime.Immutable
import com.aivanovski.leetcode.android.entity.ErrorMessage
import com.aivanovski.leetcode.android.presentation.quiz.cells.viewModel.QuestionCardCellViewModel

@Immutable
sealed interface QuizState {

    @Immutable
    data object Loading : QuizState

    @Immutable
    data class Error(
        val error: ErrorMessage
    ) : QuizState

    @Immutable
    data class Card(
        val cardViewModel: QuestionCardCellViewModel,
        val questionNumber: Int,
        val totalQuestions: Int
    ) : QuizState

    @Immutable
    data class Result(
        val questionsAnswered: Int,
        val positivelyAnswered: Int,
        val negativelyAnswered: Int
    ) : QuizState
}