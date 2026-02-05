package com.github.ai.leetcodequiz.api.response

import kotlinx.serialization.Serializable
import com.github.ai.leetcodequiz.api.UserDto

@Serializable
data class SignupResponse(
    val token: String,
    val user: UserDto
)