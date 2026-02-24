package com.aivanovski.leetcode.android.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.aivanovski.leetcode.android.data.api.ApiClient
import com.aivanovski.leetcode.android.data.settings.Settings
import com.aivanovski.leetcode.android.entity.exception.AppException

class AuthRepository(
    private val api: ApiClient,
    private val settings: Settings
) {

    fun isLoggedIn(): Boolean {
        return settings.userEmail != null && settings.userPassword != null
    }

    suspend fun login(
        email: String,
        password: String
    ): Either<AppException, Unit> =
        either {
            val response = api.login(email, password).bind()

            settings.userEmail = email
            settings.userPassword = password
            settings.authToken = response.token
        }

    fun logout() {
        settings.authToken = null
        settings.userEmail = null
        settings.userPassword = null
    }
}