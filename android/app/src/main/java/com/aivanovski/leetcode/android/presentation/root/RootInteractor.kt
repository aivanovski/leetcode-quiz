package com.aivanovski.leetcode.android.presentation.root

import com.aivanovski.leetcode.android.data.repository.AuthRepository

class RootInteractor(
    private val authRepository: AuthRepository
) {

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()
}