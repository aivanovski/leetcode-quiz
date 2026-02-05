package com.aivanovski.leetcode.android.entity.exception

import com.github.ai.leetcodequiz.api.ErrorMessageDto
import io.ktor.http.HttpStatusCode

open class AppException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)

open class ParsingException(
    message: String? = null,
    cause: Throwable? = null
) : AppException(message, cause)

open class ApiException(
    message: String? = null,
    cause: Throwable? = null,
    val errorResponse: ErrorMessageDto? = null,
    val status: HttpStatusCode? = null
) : AppException(message, cause)

class NetworkException(
    cause: Throwable? = null
) : ApiException(cause = cause)