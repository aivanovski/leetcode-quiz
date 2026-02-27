package com.aivanovski.leetcode.android.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_entities")
data class SyncEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val type: SyncEntityType,
    val timestamp: Long
)