package com.aivanovski.leetcode.android.data.api.converters

import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.Question
import com.aivanovski.leetcode.android.entity.Questionnaire
import com.github.ai.leetcodequiz.api.ProblemItemDto
import com.github.ai.leetcodequiz.api.ProblemsItemDto
import com.github.ai.leetcodequiz.api.QuestionItemDto
import com.github.ai.leetcodequiz.api.QuestionnaireItemDto
import com.github.ai.leetcodequiz.api.QuestionnairesItemDto

fun ProblemsItemDto.toProblem(): Problem =
    Problem(
        id = id,
        title = title,
        content = "",
        hints = emptyList(),
        categoryTitle = categoryTitle,
        difficulty = difficulty,
        url = url,
        likes = likes.toLong(),
        dislikes = dislikes.toLong()
    )

fun ProblemItemDto.toProblem(): Problem =
    Problem(
        id = id,
        title = title,
        content = content,
        hints = hints,
        categoryTitle = categoryTitle,
        difficulty = difficulty,
        url = url,
        likes = likes.toLong(),
        dislikes = dislikes.toLong()
    )

fun QuestionnaireItemDto.toQuestionnaire(): Questionnaire =
    Questionnaire(
        id = id,
        isComplete = isComplete,
        questions = nextQuestions.map { question -> question.toQuestion() },
        stats = stats
    )

fun QuestionnairesItemDto.toQuestionnaire(): Questionnaire =
    Questionnaire(
        id = id,
        isComplete = isComplete,
        questions = nextQuestions.map { question -> question.toQuestion() },
        stats = stats
    )

fun QuestionItemDto.toQuestion(): Question =
    Question(
        id = id,
        problemId = problemId,
        question = question
    )