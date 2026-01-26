package com.aivanovski.leetcode.android.data.api

import arrow.core.Either
import com.aivanovski.leetcode.android.entity.exception.ApiException
import com.aivanovski.leetcode.android.entity.exception.NetworkException
import com.github.ai.leetcodequiz.api.ErrorMessageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import java.io.IOException
import kotlinx.serialization.SerializationException
import timber.log.Timber

suspend inline fun <reified Req, reified Resp> HttpClient.httpPost(
    url: String,
    body: Req
) = send<Req, Resp>(RequestType.POST, url, body)

suspend inline fun <reified Resp> HttpClient.httpGet(url: String) =
    send<Unit, Resp>(RequestType.GET, url, Unit)

suspend inline fun <reified Req, reified Resp> HttpClient.send(
    type: RequestType,
    url: String,
    body: Req
): Either<ApiException, Resp> =
    Either
        .catch {
            val response = when (type) {
                RequestType.GET -> get(urlString = url)
                RequestType.POST -> {
                    post(urlString = url) {
                        contentType(ContentType.Application.Json)
                        setBody(body)
                    }
                }
            }

            if (!response.status.isSuccess()) {
                val errorBody = try {
                    response.body<ErrorMessageDto>()
                } catch (err: SerializationException) {
                    Timber.d(err, "Failed to parse error response:")
                    null
                }

                throw ApiException(
                    message = errorBody?.message
                        ?: "Invalid response status code: ${response.status.value}",
                    cause = null,
                    errorResponse = errorBody,
                    status = response.status
                )
            }

            response.body<Resp>()
        }
        .mapLeft { error ->
            when (error) {
                is ApiException -> error
                is IOException -> NetworkException(cause = error)
                else -> ApiException(cause = error)
            }
        }

enum class RequestType {
    GET,
    POST
}