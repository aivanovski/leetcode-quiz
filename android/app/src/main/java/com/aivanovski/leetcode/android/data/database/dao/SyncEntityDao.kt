package com.aivanovski.leetcode.android.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.aivanovski.leetcode.android.data.database.model.SyncEntity
import com.aivanovski.leetcode.android.data.database.model.SyncEntityType

@Dao
interface SyncEntityDao {

    @Query("SELECT * FROM sync_entities")
    fun getAll(): List<SyncEntity>

    fun getByEntityType(type: SyncEntityType): SyncEntity? {
        return getAll().firstOrNull { it.type == type }
    }

    @Insert
    fun insert(entity: SyncEntity)

    @Update
    fun update(entity: SyncEntity)

    @Query("DELETE FROM sync_entities")
    fun removeAll()
}