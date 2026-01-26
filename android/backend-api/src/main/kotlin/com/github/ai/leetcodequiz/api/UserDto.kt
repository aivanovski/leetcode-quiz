package com.github.ai.leetcodequiz.api

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val name: String,
    val email: String
)