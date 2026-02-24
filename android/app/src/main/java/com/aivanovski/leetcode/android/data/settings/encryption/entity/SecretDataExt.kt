package com.aivanovski.leetcode.android.data.settings.encryption.entity

import com.aivanovski.leetcode.android.utils.Base64Utils

fun Base64SecretData.toSecretData(): SecretData? {
    val initVector = Base64Utils.decodeToBytes(initVector).getOrNull()
        ?: return null

    val data = Base64Utils.decodeToBytes(encryptedText).getOrNull()
        ?: return null

    return SecretData(
        initVector = initVector,
        encryptedData = data
    )
}

fun SecretData.toBase64SecretData(): Base64SecretData {
    return Base64SecretData(
        initVector = Base64Utils.encode(initVector),
        encryptedText = Base64Utils.encode(encryptedData)
    )
}