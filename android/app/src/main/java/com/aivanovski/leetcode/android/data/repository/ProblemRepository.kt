package com.aivanovski.leetcode.android.data.repository

import arrow.core.Either
import arrow.core.right
import com.aivanovski.leetcode.android.data.api.ApiClient
import com.aivanovski.leetcode.android.data.database.dao.ProblemEntityDao
import com.aivanovski.leetcode.android.data.database.toDatabaseEntity
import com.aivanovski.leetcode.android.data.database.toDomainEntity
import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.utils.unwrap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class ProblemRepository(
    private val dao: ProblemEntityDao,
    private val api: ApiClient
) {

    fun getById(id: Int): Flow<Either<AppException, Problem>> =
        channelFlow {
            val localProblem = dao.getById(id)
            if (localProblem != null) {
                send(localProblem.toDomainEntity().right())
            }

            val getProblemResult = api.getProblemById(id.toString())

            if (getProblemResult.isLeft()) {
                if (localProblem == null) {
                    send(getProblemResult)
                }
                return@channelFlow
            }

            val remoteProblem = getProblemResult.unwrap()
            if (localProblem != null) {
                if (localProblem.toDomainEntity() != remoteProblem) {
                    dao.updateWithInnerEntities(remoteProblem.toDatabaseEntity())
                }
            } else {
                dao.insertWithInnerEntities(remoteProblem.toDatabaseEntity())
            }

            send(remoteProblem.right())
        }
}