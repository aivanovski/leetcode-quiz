package com.aivanovski.leetcode.android.data.database

import com.aivanovski.leetcode.android.data.database.model.HintEntity
import com.aivanovski.leetcode.android.data.database.model.ProblemWithInnerEntities
import com.aivanovski.leetcode.android.data.database.model.ProblemEntity
import com.aivanovski.leetcode.android.data.database.model.QuestionEntity
import com.aivanovski.leetcode.android.data.database.model.SolutionEntity
import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.Question

fun Problem.toDatabaseEntity(): ProblemWithInnerEntities =
    ProblemWithInnerEntities(
        problem = ProblemEntity(
            id = id,
            title = title,
            content = content,
            categoryTitle = categoryTitle,
            difficulty = difficulty,
            url = url,
            likes = likes,
            dislikes = dislikes
        ),
        solutions = solutions.map { solution ->
            SolutionEntity(
                problemId = id,
                content = solution
            )
        },
        questions = questions.map { question ->
            QuestionEntity(
                problemId = id,
                uid = question.uid,
                content = question.question
            )
        },
        hints = hints.map { hint ->
            HintEntity(
                problemId = id,
                content = hint
            )
        }
    )

fun ProblemWithInnerEntities.toDomainEntity(): Problem =
    Problem(
        id = problem.id ?: 0,
        title = problem.title,
        content = problem.content,
        hints = hints.map { it.content },
        categoryTitle = problem.categoryTitle,
        difficulty = problem.difficulty,
        solutions = solutions.map { it.content },
        questions = questions.map {
            Question(
                uid = it.uid,
                problemId = it.problemId,
                question = it.content
            )
        },
        url = problem.url,
        likes = problem.likes,
        dislikes = problem.dislikes
    )