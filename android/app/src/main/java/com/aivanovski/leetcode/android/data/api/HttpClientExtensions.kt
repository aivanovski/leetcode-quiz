package com.aivanovski.leetcode.android.data.api

import arrow.core.Either
import com.aivanovski.leetcode.android.entity.exception.ApiException
import com.aivanovski.leetcode.android.entity.exception.NetworkException
import com.github.ai.leetcodequiz.api.ProtobufResponseMapper
import com.github.ai.leetcodequiz.api.ResponseDto
import com.google.protobuf.MessageLite
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import java.io.IOException

suspend inline fun <reified Req : MessageLite, reified Resp> HttpClient.httpPost(
    url: String,
    body: Req
) = send<Resp>(RequestType.POST, url, body)

suspend inline fun <reified Resp> HttpClient.httpGet(url: String) = send<Resp>(RequestType.GET, url)

suspend inline fun <reified Resp> HttpClient.send(
    type: RequestType,
    url: String,
    requestBody: MessageLite? = null
): Either<ApiException, Resp> =
    Either
        .catch {
            val response = when (type) {
                RequestType.GET -> get(urlString = url)
                RequestType.POST -> post(urlString = url) {
                    contentType(ContentType.Application.OctetStream)
                    setBody(requireNotNull(requestBody).toByteArray())
                }
            }

            val responseDto = ResponseDto.parseFrom(response.body<ByteArray>())
            if (!response.status.isSuccess()) {
                val errorBody = responseDto.errorMessageDto
                    .takeIf { responseDto.hasErrorMessageDto() }

                throw ApiException(
                    message = errorBody?.message
                        ?: "Invalid response status code: ${response.status.value}",
                    errorResponse = errorBody,
                    status = response.status
                )
            }

            ProtobufResponseMapper.getResponseBody<Resp>(responseDto)
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