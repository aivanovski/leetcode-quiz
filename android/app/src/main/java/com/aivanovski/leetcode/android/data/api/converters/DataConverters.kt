package com.aivanovski.leetcode.android.data.api.converters

import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.ProblemWithContent
import com.aivanovski.leetcode.android.entity.Question
import com.aivanovski.leetcode.android.entity.QuestionAnswer
import com.aivanovski.leetcode.android.entity.Questionnaire
import com.aivanovski.leetcode.android.entity.QuestionnaireListItem
import com.aivanovski.leetcode.android.utils.Base64Utils
import com.github.ai.leetcodequiz.api.ProblemItemDto
import com.github.ai.leetcodequiz.api.ProblemsItemDto
import com.github.ai.leetcodequiz.api.QuestionItemDto
import com.github.ai.leetcodequiz.api.QuestionnaireItemDto
import com.github.ai.leetcodequiz.api.QuestionnairesItemDto

fun ProblemsItemDto.toProblem(): Problem =
    Problem(
        id = id,
        title = title,
        categoryTitle = categoryTitle,
        difficulty = difficulty,
        url = url
    )

fun ProblemItemDto.toProblemWithContent(): ProblemWithContent =
    ProblemWithContent(
        problem = Problem(
            id = id,
            title = title,
            categoryTitle = categoryTitle,
            difficulty = difficulty,
            url = url
        ),
        content = content,
        hints = hints,
        solutions = solutions.mapNotNull { solution ->
            Base64Utils.decode(solution.contentBase64).getOrNull()
        },
        questions = questions.map { it.toQuestion() }
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