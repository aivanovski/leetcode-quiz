package com.aivanovski.leetcode.android.presentation.settings

import com.aivanovski.leetcode.android.data.api.ApiClient

class SettingsInteractor(
    private val api: ApiClient
) {

    fun reCreateHttpClient() {
        api.reCreateHttpClient()
    }
}