package com.aivanovski.leetcode.android.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aivanovski.leetcode.android.data.database.dao.ProblemEntityDao
import com.aivanovski.leetcode.android.data.database.model.HintEntity
import com.aivanovski.leetcode.android.data.database.model.ProblemEntity
import com.aivanovski.leetcode.android.data.database.model.QuestionEntity
import com.aivanovski.leetcode.android.data.database.model.SolutionEntity

@Database(
    entities = [
        ProblemEntity::class,
        SolutionEntity::class,
        QuestionEntity::class,
        HintEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract val problemDao: ProblemEntityDao

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