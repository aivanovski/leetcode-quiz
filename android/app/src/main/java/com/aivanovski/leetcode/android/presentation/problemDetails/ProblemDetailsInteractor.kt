package com.aivanovski.leetcode.android.presentation.problemDetails

import arrow.core.Either
import arrow.core.raise.either
import com.aivanovski.leetcode.android.data.api.ApiClient
import com.aivanovski.leetcode.android.domain.ProblemHtmlFormatter
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProblemDetailsInteractor(
    private val api: ApiClient,
    private val htmlFormatter: ProblemHtmlFormatter
) {

    suspend fun loadData(problemId: String): Either<AppException, ProblemDetailsData> =
        either {
            withContext(Dispatchers.IO) {
                val problem = api.getProblemById(problemId).bind()

                ProblemDetailsData(
                    problem = problem,
                    htmlContent = htmlFormatter.formatProblemHtml(problem.content)
                )
            }
        }
}