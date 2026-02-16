package com.aivanovski.leetcode.android.domain.usecases

import com.aivanovski.leetcode.android.BuildConfig

class GetDebugCredentialsUseCase {

    fun getDebugCredentials(): List<DebugCredentials> {
        val emails = BuildConfig.DEBUG_EMAILS
        val passwords = BuildConfig.DEBUG_PASSWORDS

        if (emails.isEmpty() || passwords.isEmpty()) {
            return emptyList()
        }

        return emails.zip(passwords).map { (email, password) ->
            DebugCredentials(
                email = email,
                password = password
            )
        }
    }

    data class DebugCredentials(
        val email: String,
        val password: String
    )
}