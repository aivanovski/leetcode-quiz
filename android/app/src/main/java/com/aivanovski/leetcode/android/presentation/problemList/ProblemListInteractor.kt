package com.aivanovski.leetcode.android.presentation.problemList

import arrow.core.Either
import com.aivanovski.leetcode.android.data.repository.ProblemRepository
import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.exception.AppException
import kotlinx.coroutines.flow.Flow

class ProblemListInteractor(
    private val repository: ProblemRepository
) {

    fun getProblems(isForceReload: Boolean): Flow<Either<AppException, List<Problem>>> =
        repository.getAll(isForceReload = isForceReload)
}