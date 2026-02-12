package com.aivanovski.leetcode.android.data.api.converters

import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.Question
import com.aivanovski.leetcode.android.entity.QuestionAnswer
import com.aivanovski.leetcode.android.entity.Questionnaire
import com.aivanovski.leetcode.android.entity.QuestionnaireListItem
import com.github.ai.leetcodequiz.api.ProblemItemDto
import com.github.ai.leetcodequiz.api.ProblemsItemDto
import com.github.ai.leetcodequiz.api.QuestionItemDto
import com.github.ai.leetcodequiz.api.QuestionnaireItemDto
import com.github.ai.leetcodequiz.api.QuestionnairesItemDto
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.io.bytestring.decodeToByteString
import kotlinx.io.bytestring.decodeToString
import kotlinx.io.bytestring.encodeToByteString

fun ProblemsItemDto.toProblem(): Problem =
    Problem(
        id = id,
        title = title,
        content = "",
        hints = emptyList(),
        categoryTitle = categoryTitle,
        difficulty = difficulty,
        solutions = listOf(),
        questions = listOf(),
        url = url,
        likes = likes.toLong(),
        dislikes = dislikes.toLong()
    )

@OptIn(ExperimentalEncodingApi::class)
fun ProblemItemDto.toProblem(): Problem =
    Problem(
        id = id,
        title = title,
        content = content,
        hints = hints,
        categoryTitle = categoryTitle,
        difficulty = difficulty,
        solutions = solutions.map { solution ->
            Base64.decodeToByteString(
                solution.contentBase64.encodeToByteString()
            ).decodeToString()
        },
        questions = questions.map { it.toQuestion() },
        url = url,
        likes = likes.toLong(),
        dislikes = dislikes.toLong(),
    )

fun QuestionnaireItemDto.toQuestionnaire(): Questionnaire =
    Questionnaire(
        id = id,
        isComplete = isComplete,
        questions = questions.map { question -> question.toQuestion() },
        answers = answers.map { (id, answer) -> QuestionAnswer(id, answer) },
        stats = stats
    )

fun QuestionnairesItemDto.toQuestionnaireListItem(): QuestionnaireListItem =
    QuestionnaireListItem(
        id = id,
        isComplete = isComplete,
        questionsIds = questions
    )

fun QuestionItemDto.toQuestion(): Question =
    Question(
        uid = id,
        problemId = problemId,
        question = question
    )