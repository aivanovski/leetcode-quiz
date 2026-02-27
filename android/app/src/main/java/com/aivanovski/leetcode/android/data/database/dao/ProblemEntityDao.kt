package com.aivanovski.leetcode.android.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.aivanovski.leetcode.android.data.database.model.ContentEntity
import com.aivanovski.leetcode.android.data.database.model.HintEntity
import com.aivanovski.leetcode.android.data.database.model.ProblemEntity
import com.aivanovski.leetcode.android.data.database.model.ProblemWithInnerEntities
import com.aivanovski.leetcode.android.data.database.model.QuestionEntity
import com.aivanovski.leetcode.android.data.database.model.SolutionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProblemEntityDao {

    @Transaction
    @Query("SELECT * FROM problems WHERE id = :id")
    fun getById(id: Int): ProblemWithInnerEntities?

    @Query("SELECT * FROM problems")
    fun getAll(): List<ProblemEntity>

    @Query("SELECT * FROM problems")
    fun getAllFlow(): Flow<List<ProblemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProblem(problem: ProblemEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateProblem(problem: ProblemEntity)

    @Insert
    fun insertProblems(problems: List<ProblemEntity>)

    @Insert
    suspend fun insertSolutions(questions: List<SolutionEntity>)

    @Insert
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert
    suspend fun insertHints(questions: List<HintEntity>)

    @Insert
    suspend fun insertContent(content: ContentEntity)

    @Query("DELETE FROM problems WHERE id = :id")
    fun removeById(id: Int)

    @Query("DELETE FROM solutions WHERE problem_id = :problemId")
    fun removeSolutionsByProblemId(problemId: Int)

    @Query("DELETE FROM questions WHERE problem_id = :problemId")
    fun removeQuestionsByProblemId(problemId: Int)

    @Query("DELETE FROM hints WHERE problem_id = :problemId")
    fun removeHintsByProblemId(problemId: Int)

    @Query("DELETE FROM contents WHERE problem_id = :problemId")
    fun removeContentByProblemId(problemId: Int)

    @Query("DELETE FROM problems")
    fun removeAllProblems()

    @Query("DELETE FROM contents")
    fun removeAllContents()

    @Query("DELETE FROM solutions")
    fun removeAllSolutions()

    @Query("DELETE FROM questions")
    fun removeAllQuestions()

    @Query("DELETE FROM hints")
    fun removeAllHints()

    @Transaction
    suspend fun insertWithInnerEntities(problem: ProblemWithInnerEntities) {
        insertProblem(problem.problem)

        if (problem.content != null) {
            insertContent(problem.content)
        }
        insertSolutions(problem.solutions)
        insertQuestions(problem.questions)
        insertHints(problem.hints)
    }

    @Transaction
    suspend fun updateWithInnerEntities(problem: ProblemWithInnerEntities) {
        val problemId = problem.problem.id

        removeById(problemId)
        removeContentByProblemId(problemId)
        removeSolutionsByProblemId(problemId)
        removeQuestionsByProblemId(problemId)
        removeHintsByProblemId(problemId)

        insertWithInnerEntities(problem)
    }

    @Transaction
    suspend fun clear() {
        removeAllContents()
        removeAllSolutions()
        removeAllQuestions()
        removeAllHints()
        removeAllProblems()
    }
}