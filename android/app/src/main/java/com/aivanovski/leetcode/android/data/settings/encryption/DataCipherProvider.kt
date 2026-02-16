package com.aivanovski.leetcode.android.data.settings.encryption

interface DataCipherProvider {
    fun getCipher(): DataCipher
}