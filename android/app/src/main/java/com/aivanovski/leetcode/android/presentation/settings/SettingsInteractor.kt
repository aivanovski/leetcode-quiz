package com.aivanovski.leetcode.android.presentation.settings

import com.aivanovski.leetcode.android.data.api.ApiClient
import com.aivanovski.leetcode.android.data.repository.AuthRepository
import com.aivanovski.leetcode.android.data.repository.ProblemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingsInteractor(
    private val problemRepository: ProblemRepository,
    private val api: ApiClient,
    private val authRepository: AuthRepository
) {

    fun reCreateHttpClient() {
        api.reCreateHttpClient()
    }

    suspend fun logout() =
        withContext(Dispatchers.IO) {
            authRepository.logout()
            problemRepository.clear()
        }
}