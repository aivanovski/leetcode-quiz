package com.aivanovski.leetcode.android.data.repository

import arrow.core.Either
import arrow.core.right
import com.aivanovski.leetcode.android.data.api.ApiClient
import com.aivanovski.leetcode.android.data.database.dao.ProblemEntityDao
import com.aivanovski.leetcode.android.data.database.dao.SyncEntityDao
import com.aivanovski.leetcode.android.data.database.model.ProblemEntity
import com.aivanovski.leetcode.android.data.database.model.SyncEntity
import com.aivanovski.leetcode.android.data.database.model.SyncEntityType
import com.aivanovski.leetcode.android.data.database.toDatabaseEntities
import com.aivanovski.leetcode.android.data.database.toDatabaseEntity
import com.aivanovski.leetcode.android.data.database.toDomainEntities
import com.aivanovski.leetcode.android.data.database.toDomainEntity
import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.ProblemWithContent
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.utils.unwrap
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import timber.log.Timber

class ProblemRepository(
    private val syncDao: SyncEntityDao,
    private val dao: ProblemEntityDao,
    private val api: ApiClient
) {

    fun getById(id: Int): Flow<Either<AppException, ProblemWithContent>> =
        channelFlow {
            val localProblem = dao.getById(id)
            val hasContent = (localProblem != null && localProblem.content != null)
            if (hasContent) {
                send(localProblem.toDomainEntity().right())
            }

            val getProblemResult = api.getProblemById(id.toString())

            if (getProblemResult.isLeft()) {
                if (!hasContent) {
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

    fun getAll(isForceReload: Boolean): Flow<Either<AppException, List<Problem>>> =
        channelFlow {
            val localProblems = dao.getAll()
            if (localProblems.isNotEmpty()) {
                send(localProblems.toDomainEntities().right())
            }

            val lastSync = syncDao.getByEntityType(SyncEntityType.PROBLEM)
            val timeSinceLastSync = System.currentTimeMillis() - (lastSync?.timestamp ?: 0)
            val shouldSync = (
                localProblems.isEmpty() ||
                    timeSinceLastSync > MIN_SYNC_INTERVAL_IN_MS ||
                    isForceReload
                )

            if (shouldSync) {
                val getProblemsResult = api.getProblems()
                if (getProblemsResult.isLeft()) {
                    if (localProblems.isEmpty()) {
                        send(getProblemsResult)
                    }
                    return@channelFlow
                }

                syncProblemsWithDatabase(
                    remoteProblems = getProblemsResult.unwrap().toDatabaseEntities(),
                    localProblems = localProblems
                )

                if (lastSync != null) {
                    syncDao.update(lastSync.copy(timestamp = System.currentTimeMillis()))
                } else {
                    syncDao.insert(
                        SyncEntity(
                            type = SyncEntityType.PROBLEM,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }

                send(dao.getAll().toDomainEntities().right())
            } else {
                Timber.d("Skip sync: lastSync=$lastSync")
            }
        }

    private fun syncProblemsWithDatabase(
        remoteProblems: List<ProblemEntity>,
        localProblems: List<ProblemEntity>
    ) {
        val problemIdToLocalProblemMap = localProblems.associateBy { problem -> problem.id }

        val insertions = remoteProblems.filter { remote ->
            remote.id !in problemIdToLocalProblemMap
        }
        val updates = remoteProblems.filter { remote ->
            val local = problemIdToLocalProblemMap[remote.id]
            local != null && local != remote
        }

        Timber.d("Sync problems:")
        Timber.d("    insertions: ${insertions.size}")
        Timber.d("    updates: ${updates.size}")

        for (chunk in insertions.chunked(100)) {
            dao.insertProblems(chunk)
            for (problem in chunk) {
                Timber.d("  + [${problem.id}] ${problem.title}")
            }
        }

        for (problem in updates) {
            dao.updateProblem(problem)
            Timber.d("  ~ [${problem.id}] ${problem.title}")
        }
    }

    suspend fun clear() {
        dao.clear()
        syncDao.removeAll()
    }

    companion object {
        private val MIN_SYNC_INTERVAL_IN_MS = TimeUnit.HOURS.toMillis(6)
    }
}