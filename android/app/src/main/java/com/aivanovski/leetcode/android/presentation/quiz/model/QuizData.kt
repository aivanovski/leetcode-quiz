package com.aivanovski.leetcode.android.presentation.quiz.model

import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.Questionnaire

data class QuizData(
    val questionnaire: Questionnaire,
    val problems: List<Problem>
)