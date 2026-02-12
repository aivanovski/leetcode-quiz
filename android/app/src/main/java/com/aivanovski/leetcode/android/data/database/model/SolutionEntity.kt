package com.aivanovski.leetcode.android.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "solutions",
    foreignKeys = [
        ForeignKey(
            entity = ProblemEntity::class,
            parentColumns = ["id"],
            childColumns = ["problem_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("problem_id")]
)
data class SolutionEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("id") val id: Long? = null,
    @ColumnInfo("problem_id") val problemId: Int,
    val content: String
)