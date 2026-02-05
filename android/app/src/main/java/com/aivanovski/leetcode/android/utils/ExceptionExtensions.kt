package com.aivanovski.leetcode.android.utils

import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.entity.exception.ApiException
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.entity.exception.NetworkException
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider

fun Throwable.getRootCause(): Throwable? {
    var result: Throwable? = this.cause

    while (result?.cause != null) {
        val cause = result.cause
        requireNotNull(cause)
        result = cause
    }

    return result
}

fun Throwable?.hasMessage(): Boolean = this?.message?.isNotBlank() == true

fun AppException.formatReadableMessage(resources: ResourceProvider): String {
    val error = this
    val cause = getRootCause()

    return when {
        error is NetworkException -> buildString {
            append(resources.getString(R.string.network_error_message))

            if (cause.hasMessage()) {
                append(": ")
                append(cause?.message)
            }
        }

        error is ApiException -> buildString {
            if (error.errorResponse?.message?.isNotBlank() == true) {
                append(error.errorResponse.message)
            } else if (error.status != null) {
                append(
                    resources.getString(
                        R.string.http_status_code_with_str,
                        error.status.value,
                        error.status
                    )
                )
            } else {
                append(resources.getString(R.string.unknown_error_message))
            }
        }

        cause.hasMessage() -> cause?.message ?: StringUtils.EMPTY
        error.hasMessage() -> error.message ?: StringUtils.EMPTY
        else -> resources.getString(R.string.unknown_error_message)
    }
}