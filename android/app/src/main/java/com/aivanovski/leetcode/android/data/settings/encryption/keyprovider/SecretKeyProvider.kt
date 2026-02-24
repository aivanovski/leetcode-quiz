package com.aivanovski.leetcode.android.data.settings.encryption.keyprovider

import javax.crypto.SecretKey

interface SecretKeyProvider {
    fun getSecretKey(isCreateIfNeed: Boolean): SecretKey?
}