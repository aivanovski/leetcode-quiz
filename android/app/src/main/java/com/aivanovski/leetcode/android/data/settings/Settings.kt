package com.aivanovski.leetcode.android.data.settings

import android.content.Context
import com.aivanovski.leetcode.android.data.api.ServerUrls
import com.aivanovski.leetcode.android.data.settings.SettingsImpl.Keys.AUTH_TOKEN
import com.aivanovski.leetcode.android.data.settings.SettingsImpl.Keys.HTTP_LOG_LEVEL
import com.aivanovski.leetcode.android.data.settings.SettingsImpl.Keys.IS_VALIDATE_SSL_CERTIFICATE
import com.aivanovski.leetcode.android.data.settings.SettingsImpl.Keys.SERVER_URL
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
    context: Context
) : Settings {

    private val prefs = KsPrefs(context.applicationContext)

    override var authToken: String?
        get() = prefs.pull(AUTH_TOKEN.key, EMPTY).ifEmpty { null }
        set(value) = prefs.push(AUTH_TOKEN.key, value.orEmpty())

    // TODO: remove default credentials

    override var userEmail: String?
        get() = "admin@mail.com"
        set(value) {}

    override var userPassword: String?
        get() = "abc123"
        set(value) {}

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

    enum class Keys {
        AUTH_TOKEN,
        SERVER_URL,
        HTTP_LOG_LEVEL,
        IS_VALIDATE_SSL_CERTIFICATE;

        val key: String = name.lowercase()
    }
}