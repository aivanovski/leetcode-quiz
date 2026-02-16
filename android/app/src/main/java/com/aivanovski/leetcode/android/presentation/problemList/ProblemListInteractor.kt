package com.aivanovski.leetcode.android.presentation.problemList

import arrow.core.Either
import arrow.core.raise.either
import com.aivanovski.leetcode.android.data.api.ApiClient
import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.exception.AppException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProblemListInteractor(
    private val api: ApiClient
) {

    suspend fun getProblems(): Either<AppException, List<Problem>> =
        either {
            withContext(Dispatchers.IO) {
                api.getProblems().bind()
            }
        }
}