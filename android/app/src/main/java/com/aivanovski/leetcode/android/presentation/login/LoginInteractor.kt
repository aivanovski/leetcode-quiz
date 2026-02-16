package com.aivanovski.leetcode.android.presentation.login

import arrow.core.Either
import com.aivanovski.leetcode.android.data.repository.AuthRepository
import com.aivanovski.leetcode.android.domain.usecases.GetDebugCredentialsUseCase
import com.aivanovski.leetcode.android.entity.exception.AppException

class LoginInteractor(
    private val authRepository: AuthRepository,
    private val getDebugCredentialsUseCase: GetDebugCredentialsUseCase
) {

    suspend fun login(
        email: String,
        password: String
    ): Either<AppException, Unit> = authRepository.login(email, password)

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()

    fun getDebugCredentials() = getDebugCredentialsUseCase.getDebugCredentials()
}