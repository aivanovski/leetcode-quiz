package com.aivanovski.leetcode.android.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "problems")
data class ProblemEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("id")
    val id: Int,
    val title: String,
    @ColumnInfo("category_title")
    val categoryTitle: String,
    val difficulty: String,
    val url: String
)