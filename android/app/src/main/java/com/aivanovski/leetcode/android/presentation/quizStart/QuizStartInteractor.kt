package com.aivanovski.leetcode.android.presentation.quizStart

import arrow.core.Either
import arrow.core.raise.either
import com.aivanovski.leetcode.android.data.api.ApiClient
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.presentation.quizStart.model.QuizStartData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizStartInteractor(
    private val api: ApiClient
) {

    suspend fun loadQuestionnaire(): Either<AppException, QuizStartData> =
        either {
            withContext(Dispatchers.IO) {
                val questionnaires = api.getQuestionnaires().bind()

                val questionnaireId = questionnaires
                    .first { questionnaire ->
                        !questionnaire.isComplete
                    }
                    .id // TODO: return error if not found

                val questionnaire = api.getQuestionnaire(questionnaireId).bind()

                QuizStartData(
                    questionnaire = questionnaire
                )
            }
        }
}