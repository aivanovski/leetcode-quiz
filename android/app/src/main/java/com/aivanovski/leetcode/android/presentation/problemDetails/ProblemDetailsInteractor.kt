package com.aivanovski.leetcode.android.presentation.problemDetails

import arrow.core.Either
import arrow.core.raise.either
import com.aivanovski.leetcode.android.data.repository.ProblemRepository
import com.aivanovski.leetcode.android.domain.ProblemHtmlFormatter
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProblemDetailsInteractor(
    private val repository: ProblemRepository,
    private val htmlFormatter: ProblemHtmlFormatter
) {

    fun loadData(problemId: Int): Flow<Either<AppException, ProblemDetailsData>> =
        repository.getById(problemId)
            .map { problemResult ->
                either {
                    val problem = problemResult.bind()

                    ProblemDetailsData(
                        problem = problem,
                        htmlContent = htmlFormatter.formatProblemHtml(problem.content)
                    )
                }
            }
}