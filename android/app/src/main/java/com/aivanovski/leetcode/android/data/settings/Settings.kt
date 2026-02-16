package com.aivanovski.leetcode.android.data.settings

import android.content.Context
import com.aivanovski.leetcode.android.data.api.ServerUrls
import com.aivanovski.leetcode.android.data.settings.SettingsImpl.SettingKey.AUTH_TOKEN
import com.aivanovski.leetcode.android.data.settings.SettingsImpl.SettingKey.HTTP_LOG_LEVEL
import com.aivanovski.leetcode.android.data.settings.SettingsImpl.SettingKey.IS_VALIDATE_SSL_CERTIFICATE
import com.aivanovski.leetcode.android.data.settings.SettingsImpl.SettingKey.SERVER_URL
import com.aivanovski.leetcode.android.data.settings.SettingsImpl.SettingKey.USER_EMAIL
import com.aivanovski.leetcode.android.data.settings.SettingsImpl.SettingKey.USER_PASSWORD
import com.aivanovski.leetcode.android.data.settings.encryption.DataCipherProvider
import com.aivanovski.leetcode.android.utils.StringUtils.EMPTY
import com.cioccarellia.ksprefs.KsPrefs
import io.ktor.client.plugins.logging.LogLevel

interface Settings {
    var authToken: String?
    var serverUrl: String
    var httpLogLevel: LogLevel
    var userEmail: String?
    var userPassword: String?
    var isValidateSslCertificate: Boolean
}

class SettingsImpl(
    context: Context,
    private val dataCipherProvider: DataCipherProvider,
) : Settings {

    private val cipher by lazy {
        dataCipherProvider.getCipher()
    }

    private val prefs = KsPrefs(
        appContext = context.applicationContext,
        namespace = "app-settings"
    )

    override var authToken: String?
        get() = prefs.pullEncoded(AUTH_TOKEN.key)
        set(value) = prefs.pushEncoded(AUTH_TOKEN.key, value)

    override var userEmail: String?
        get() = prefs.pullEncoded(USER_EMAIL.key)
        set(value) = prefs.pushEncoded(USER_EMAIL.key, value)

    override var userPassword: String?
        get() = prefs.pullEncoded(USER_PASSWORD.key)
        set(value) = prefs.pushEncoded(USER_PASSWORD.key, value)

    override var serverUrl: String
        get() = prefs.pull(SERVER_URL.key, ServerUrls.PROD_SERVER_URL)
        set(value) = prefs.push(SERVER_URL.key, value)

    override var isValidateSslCertificate: Boolean
        get() = prefs.pull(IS_VALIDATE_SSL_CERTIFICATE.key, true)
        set(value) = prefs.push(IS_VALIDATE_SSL_CERTIFICATE.key, value)

    override var httpLogLevel: LogLevel
        get() = prefs.pull(HTTP_LOG_LEVEL.key, EMPTY)
            .let { name ->
                LogLevel.entries.find { level -> level.name == name }
                    ?: LogLevel.INFO
            }
        set(value) = prefs.push(HTTP_LOG_LEVEL.key, value.name)

    private fun KsPrefs.pullEncoded(key: String): String? {
        val value = prefs.pull(key, EMPTY)
        return if (value.isNotEmpty()) cipher.decode(value) else null
    }

    private fun KsPrefs.pushEncoded(key: String, value: String?) {
        val encoded = value?.let { cipher.encode(it) } ?: EMPTY
        prefs.push(key, encoded)
    }

    enum class SettingKey {
        AUTH_TOKEN,
        USER_EMAIL,
        USER_PASSWORD,
        SERVER_URL,
        HTTP_LOG_LEVEL,
        IS_VALIDATE_SSL_CERTIFICATE;

        val key: String = name.lowercase()
    }
}