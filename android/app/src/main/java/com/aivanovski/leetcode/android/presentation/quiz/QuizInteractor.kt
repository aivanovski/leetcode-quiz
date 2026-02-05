package com.aivanovski.leetcode.android.presentation.quiz

import arrow.core.Either
import arrow.core.raise.either
import com.aivanovski.leetcode.android.data.api.ApiClient
import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.Questionnaire
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.presentation.quiz.model.Answer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizInteractor(
    private val api: ApiClient
) {

    suspend fun loadQuestionnaire(): Either<AppException, Questionnaire> =
        either {
            withContext(Dispatchers.IO) {
                val questionnaires = api.getQuestionnaires().bind()

                val questionnaireId = questionnaires
                    .first { questionnaire ->
                        !questionnaire.isComplete
                    }
                    .id // TODO: return error if not found

                api.getQuestionnaire(questionnaireId).bind()
            }
        }

    suspend fun loadProblem(
        problemId: Int
    ): Either<AppException, Problem> =
        either {
            withContext(Dispatchers.IO) {
                api.getProblemById(problemId.toString()).bind()
            }
        }

    suspend fun answerAndLoadMore(
        questionnaireId: String,
        questionId: String,
        answer: Answer
    ): Either<AppException, Questionnaire> =
        either {
            withContext(Dispatchers.IO) {
                val apiAnswer = when (answer) {
                    Answer.BAD -> -1
                    Answer.GOOD -> 1
                }

                api.postAnswer(
                    questionnaireId,
                    questionId,
                    apiAnswer
                ).bind()
            }
        }
}