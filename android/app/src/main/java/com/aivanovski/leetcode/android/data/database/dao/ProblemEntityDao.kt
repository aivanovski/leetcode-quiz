package com.aivanovski.leetcode.android.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.aivanovski.leetcode.android.data.database.model.HintEntity
import com.aivanovski.leetcode.android.data.database.model.ProblemEntity
import com.aivanovski.leetcode.android.data.database.model.ProblemWithInnerEntities
import com.aivanovski.leetcode.android.data.database.model.QuestionEntity
import com.aivanovski.leetcode.android.data.database.model.SolutionEntity

@Dao
interface ProblemEntityDao {

    @Transaction
    @Query("SELECT * FROM problems WHERE id = :id")
    fun getById(id: Int): ProblemWithInnerEntities?

    @Insert
    fun insertProblem(problem: ProblemEntity): Long

    @Insert
    suspend fun insertSolutions(questions: List<SolutionEntity>)

    @Insert
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert
    suspend fun insertHints(questions: List<HintEntity>)

    @Query("DELETE FROM problems WHERE id = :id")
    fun removeById(id: Int)

    @Query("DELETE FROM solutions WHERE problem_id = :problemId")
    fun removeSolutionsByProblemId(problemId: Int)

    @Query("DELETE FROM questions WHERE problem_id = :problemId")
    fun removeQuestionsByProblemId(problemId: Int)

    @Query("DELETE FROM hints WHERE problem_id = :problemId")
    fun removeHintsByProblemId(problemId: Int)

    @Transaction
    suspend fun insertWithInnerEntities(problem: ProblemWithInnerEntities) {
        insertProblem(problem.problem)

        insertSolutions(problem.solutions)
        insertQuestions(problem.questions)
        insertHints(problem.hints)
    }

    @Transaction
    suspend fun updateWithInnerEntities(problem: ProblemWithInnerEntities) {
        val problemId = problem.problem.id ?: 0

        removeById(problemId)
        removeSolutionsByProblemId(problemId)
        removeQuestionsByProblemId(problemId)
        removeHintsByProblemId(problemId)

        insertWithInnerEntities(problem)
    }
}