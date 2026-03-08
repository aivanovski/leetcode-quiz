package com.aivanovski.leetcode.android.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aivanovski.leetcode.android.data.database.dao.ProblemEntityDao
import com.aivanovski.leetcode.android.data.database.dao.SyncEntityDao
import com.aivanovski.leetcode.android.data.database.model.ContentEntity
import com.aivanovski.leetcode.android.data.database.model.HintEntity
import com.aivanovski.leetcode.android.data.database.model.ProblemEntity
import com.aivanovski.leetcode.android.data.database.model.QuestionEntity
import com.aivanovski.leetcode.android.data.database.model.SolutionEntity
import com.aivanovski.leetcode.android.data.database.model.SyncEntity

@Database(
    entities = [
        ProblemEntity::class,
        ContentEntity::class,
        SolutionEntity::class,
        QuestionEntity::class,
        HintEntity::class,
        SyncEntity::class
    ],
    version = 1
)
@TypeConverters(DatabaseTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val problemDao: ProblemEntityDao
    abstract val syncEntityDao: SyncEntityDao

    companion object {

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "leetcode-quiz.db"
            )
                .build()
        }
    }
}