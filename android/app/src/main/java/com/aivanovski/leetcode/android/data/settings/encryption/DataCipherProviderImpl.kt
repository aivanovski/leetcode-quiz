package com.aivanovski.leetcode.android.data.settings.encryption

import android.content.Context
import com.aivanovski.leetcode.android.data.settings.encryption.entity.CipherTransformation
import com.aivanovski.leetcode.android.data.settings.encryption.keyprovider.KeyStoreSecretKeyProvider

class DataCipherProviderImpl(private val context: Context) : DataCipherProvider {

    private val dataCipher: DataCipher by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        instantiateDataCipher()
    }

    override fun getCipher(): DataCipher {
        return dataCipher
    }

    private fun instantiateDataCipher(): DataCipher {
        return DataCipherImpl(KeyStoreSecretKeyProvider(), CipherTransformation.AES_CBC_PKCS7)
    }
}