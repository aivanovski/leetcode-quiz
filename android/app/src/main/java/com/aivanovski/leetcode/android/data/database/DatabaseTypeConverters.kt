package com.aivanovski.leetcode.android.data.database

import androidx.room.TypeConverter
import com.aivanovski.leetcode.android.data.database.model.SyncEntityType

class DatabaseTypeConverters {

    @TypeConverter
    fun fromSyncEntityType(status: SyncEntityType): String {
        return status.name
    }

    @TypeConverter
    fun toSyncEntityType(value: String): SyncEntityType {
        return SyncEntityType.valueOf(value)
    }
}