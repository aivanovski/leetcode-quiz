package com.aivanovski.leetcode.android.presentation.settings

import com.aivanovski.leetcode.android.data.api.ApiClient
import com.aivanovski.leetcode.android.data.repository.AuthRepository

class SettingsInteractor(
    private val api: ApiClient,
    private val authRepository: AuthRepository
) {

    fun reCreateHttpClient() {
        api.reCreateHttpClient()
    }

    fun logout() {
        authRepository.logout()
    }
}