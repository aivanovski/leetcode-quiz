package com.aivanovski.leetcode.android.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "problems")
data class ProblemEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("id") val id: Int? = null,
    val title: String,
    val content: String,
    @ColumnInfo("category_title") val categoryTitle: String,
    val difficulty: String,
    val url: String,
    val likes: Long,
    val dislikes: Long
    // @Relation(parentColumn = "id", entityColumn = "parentId")
    // val solutions: List<SolutionEntity>,
    // @Relation(parentColumn = "id", entityColumn = "parentId")
    // val questions: List<QuestionEntity>,
    // @Relation(parentColumn = "id", entityColumn = "parentId")
    // val hints: List<HintEntity>,
)