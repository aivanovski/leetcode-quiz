package com.aivanovski.leetcode.android.data.database

import com.aivanovski.leetcode.android.data.database.model.ContentEntity
import com.aivanovski.leetcode.android.data.database.model.HintEntity
import com.aivanovski.leetcode.android.data.database.model.ProblemEntity
import com.aivanovski.leetcode.android.data.database.model.ProblemWithInnerEntities
import com.aivanovski.leetcode.android.data.database.model.QuestionEntity
import com.aivanovski.leetcode.android.data.database.model.SolutionEntity
import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.ProblemWithContent
import com.aivanovski.leetcode.android.entity.Question
import com.aivanovski.leetcode.android.utils.StringUtils

fun ProblemWithContent.toDatabaseEntity(): ProblemWithInnerEntities =
    ProblemWithInnerEntities(
        problem = ProblemEntity(
            id = problem.id,
            title = problem.title,
            categoryTitle = problem.categoryTitle,
            difficulty = problem.difficulty,
            url = problem.url
        ),
        content = ContentEntity(
            problemId = problem.id,
            content = content
        ),
        solutions = solutions.map { solution ->
            SolutionEntity(
                problemId = problem.id,
                content = solution
            )
        },
        questions = questions.map { question ->
            QuestionEntity(
                problemId = problem.id,
                uid = question.uid,
                content = question.question
            )
        },
        hints = hints.map { hint ->
            HintEntity(
                problemId = problem.id,
                content = hint
            )
        }
    )

fun List<Problem>.toDatabaseEntities(): List<ProblemEntity> =
    this.map { problem -> problem.toDatabaseEntity() }

fun Problem.toDatabaseEntity(): ProblemEntity =
    ProblemEntity(
        id = id,
        title = title,
        categoryTitle = categoryTitle,
        difficulty = difficulty,
        url = url
    )

fun List<ProblemEntity>.toDomainEntities(): List<Problem> =
    this.map { entity -> entity.toDomainEntity() }

fun ProblemEntity.toDomainEntity(): Problem =
    Problem(
        id = id,
        title = title,
        categoryTitle = categoryTitle,
        difficulty = difficulty,
        url = url
    )

fun ProblemWithInnerEntities.toDomainEntity(): ProblemWithContent =
    ProblemWithContent(
        problem = Problem(
            id = problem.id,
            title = problem.title,
            categoryTitle = problem.categoryTitle,
            difficulty = problem.difficulty,
            url = problem.url
        ),
        content = content?.content ?: StringUtils.EMPTY,
        hints = hints.map { it.content },
        solutions = solutions.map { it.content },
        questions = questions.map {
            Question(
                uid = it.uid,
                problemId = it.problemId,
                question = it.content
            )
        }
    )