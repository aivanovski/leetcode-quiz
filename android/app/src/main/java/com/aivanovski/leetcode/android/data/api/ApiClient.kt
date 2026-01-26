package com.aivanovski.leetcode.android.data.api

import arrow.core.Either
import com.aivanovski.leetcode.android.data.api.converters.toProblem
import com.aivanovski.leetcode.android.data.api.converters.toQuestionnaire
import com.aivanovski.leetcode.android.data.settings.Settings
import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.Questionnaire
import com.aivanovski.leetcode.android.entity.exception.ApiException
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.utils.mutableStateFlow
import com.github.ai.leetcodequiz.api.request.PostSubmissionRequest
import com.github.ai.leetcodequiz.api.response.GetProblemResponse
import com.github.ai.leetcodequiz.api.response.GetProblemsResponse
import com.github.ai.leetcodequiz.api.response.GetQuestionnaireResponse
import com.github.ai.leetcodequiz.api.response.GetQuestionnairesResponse
import com.github.ai.leetcodequiz.api.response.PostSubmissionResponse
import io.ktor.client.HttpClient

class ApiClient(
    private val settings: Settings
) {

    private var urlBuilder by mutableStateFlow(createUrlBuilder())
    private var httpClient by mutableStateFlow(createHttpClient())

    fun reCreateHttpClient() {
        urlBuilder = createUrlBuilder()
        httpClient = createHttpClient()
    }

    suspend fun getProblems(): Either<AppException, List<Problem>> =
        httpClient
            .httpGet<GetProblemsResponse>(url = urlBuilder.problems())
            .map { response -> response.problems.map { it.toProblem() } }

    suspend fun getProblemById(id: String): Either<ApiException, Problem> =
        httpClient
            .httpGet<GetProblemResponse>(url = urlBuilder.problem(id))
            .map { response -> response.problem.toProblem() }

    suspend fun getQuestionnaires(): Either<ApiException, List<Questionnaire>> =
        httpClient
            .httpGet<GetQuestionnairesResponse>(url = urlBuilder.questionnaires())
            .map { response -> response.questionnaires.map { it.toQuestionnaire() } }

    suspend fun getQuestionnaire(
        id: String
    ): Either<ApiException, Pair<Questionnaire, List<Problem>>> =
        httpClient
            .httpGet<GetQuestionnaireResponse>(url = urlBuilder.questionnaire(id))
            .map { response ->
                val problems = response.questionnaire.problems.map { it.toProblem() }
                response.questionnaire.toQuestionnaire() to problems
            }

    suspend fun postAnswer(
        questionnaireId: String,
        questionId: String,
        answer: Int
    ): Either<ApiException, Questionnaire> =
        httpClient
            .httpPost<PostSubmissionRequest, PostSubmissionResponse>(
                url = urlBuilder.questionnaire(questionnaireId),
                body = PostSubmissionRequest(questionId, answer)
            )
            .map { response -> response.questionnaire.toQuestionnaire() }

    private fun createUrlBuilder(): ApiUrlBuilder =
        ApiUrlBuilder(
            baseUrl = settings.serverUrl
        )

    private fun createHttpClient(): HttpClient =
        HttpClientFactory.createHttpClient(
            settings = settings,
            isSslVerificationEnabled = settings.isValidateSslCertificate
        )
}