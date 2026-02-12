package com.aivanovski.leetcode.android.data.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class ProblemWithInnerEntities(
    @Embedded
    val problem: ProblemEntity,

    @Relation(parentColumn = "id", entityColumn = "problem_id")
    val solutions: List<SolutionEntity>,

    @Relation(parentColumn = "id", entityColumn = "problem_id")
    val questions: List<QuestionEntity>,

    @Relation(parentColumn = "id", entityColumn = "problem_id")
    val hints: List<HintEntity>,
)