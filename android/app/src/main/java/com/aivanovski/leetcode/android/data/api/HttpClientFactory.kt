package com.aivanovski.leetcode.android.data.api

import android.annotation.SuppressLint
import com.aivanovski.leetcode.android.BuildConfig
import com.aivanovski.leetcode.android.data.settings.Settings
import com.github.ai.leetcodequiz.api.request.LoginRequest
import com.github.ai.leetcodequiz.api.response.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager
import kotlinx.serialization.json.Json
import timber.log.Timber

object HttpClientFactory {

    fun createHttpClient(
        settings: Settings,
        isSslVerificationEnabled: Boolean
    ): HttpClient {
        val urlBuilder = ApiUrlBuilder(settings.serverUrl)

        return HttpClient(OkHttp) {
            if (BuildConfig.DEBUG && !isSslVerificationEnabled) {
                Timber.w("--------------------------------------------")
                Timber.w("--                                        --")
                Timber.w("--                                        --")
                Timber.w("-- SSL Certificate validation is disabled --")
                Timber.w("--                                        --")
                Timber.w("--                                        --")
                Timber.w("--------------------------------------------")
                disableSslVerification()
            }

            setupJsonContentNegotiation()
            setupAuthentication(settings, urlBuilder)
            setupLogging(settings)
        }
    }

    private fun HttpClientConfig<OkHttpConfig>.setupLogging(settings: Settings) {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Timber.d(message)
                }
            }
            level = settings.httpLogLevel
        }
    }

    private fun HttpClientConfig<OkHttpConfig>.setupJsonContentNegotiation() {
        install(ContentNegotiation) {
            json(
                Json {
                    explicitNulls = false
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    private fun HttpClientConfig<OkHttpConfig>.setupAuthentication(
        settings: Settings,
        apiUrlBuilder: ApiUrlBuilder
    ) {
        install(Auth) {
            bearer {
                loadTokens {
                    settings.authToken?.let { token ->
                        BearerTokens(accessToken = token, refreshToken = null)
                    }
                }

                refreshTokens {
                    val requestBody = LoginRequest(
                        email = settings.userEmail ?: "",
                        password = settings.userPassword ?: ""
                    )

                    val tokenResponse = client.httpPost<LoginRequest, LoginResponse>(
                        url = apiUrlBuilder.login(),
                        body = requestBody
                    ).map { it.token }

                    if (tokenResponse.isRight()) {
                        val token = tokenResponse.getOrNull()
                        settings.authToken = token
                        BearerTokens(accessToken = token ?: "", refreshToken = null)
                    } else {
                        Timber.d(tokenResponse.leftOrNull())
                        null
                    }
                }
            }
        }
    }

    private fun HttpClientConfig<OkHttpConfig>.disableSslVerification() {
        @SuppressLint("CustomX509TrustManager")
        val trustAllCerts = object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }
        }

        val sslSocketFactory = SSLContext.getInstance("SSL")
            .apply {
                init(null, arrayOf(trustAllCerts), SecureRandom())
            }
            .socketFactory

        engine {
            config {
                sslSocketFactory(sslSocketFactory, trustAllCerts)
                hostnameVerifier(hostnameVerifier = { _, _ -> true })
            }
        }
    }
}