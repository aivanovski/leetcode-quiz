package com.aivanovski.leetcode.android.presentation.core.resources

import android.content.Context
import androidx.annotation.StringRes

interface ResourceProvider {
    fun getString(@StringRes resId: Int): String
    fun getString(
        @StringRes resId: Int,
        vararg formatArgs: Any?
    ): String
}

class ResourceProviderImpl(
    private val content: Context
) : ResourceProvider {

    override fun getString(@StringRes resId: Int): String {
        return content.getString(resId)
    }

    override fun getString(
        @StringRes resId: Int,
        vararg formatArgs: Any?
    ): String {
        return content.getString(resId, *formatArgs)
    }
}