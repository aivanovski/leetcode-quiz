package com.aivanovski.leetcode.android.data.settings.encryption

interface DataCipher {
    fun encode(data: String): String?
    fun decode(data: String): String?
}