package com.aivanovski.leetcode.android.presentation.quiz

import arrow.core.Either
import arrow.core.raise.either
import com.aivanovski.leetcode.android.data.api.ApiClient
import com.aivanovski.leetcode.android.entity.exception.AppException
import com.aivanovski.leetcode.android.presentation.quiz.model.Answer
import com.aivanovski.leetcode.android.presentation.quiz.model.QuizData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizInteractor(
    private val api: ApiClient
) {

    suspend fun loadData(): Either<AppException, QuizData> =
        either {
            withContext(Dispatchers.IO) {
                val questionnaires = api.getQuestionnaires().bind()

                val activeQuestionnaire = questionnaires.first { questionnaire ->
                    !questionnaire.isComplete
                } // TODO: return error if not found

                val (_, problems) = api.getQuestionnaire(activeQuestionnaire.id).bind()

                QuizData(
                    questionnaire = activeQuestionnaire,
                    problems = problems
                )
            }
        }

    suspend fun answerAndLoadMore(
        questionnaireId: String,
        questionId: String,
        answer: Answer
    ): Either<AppException, QuizData> =
        either {
            withContext(Dispatchers.IO) {
                val apiAnswer = when (answer) {
                    Answer.BAD -> -1
                    Answer.GOOD -> 1
                }

                val questionnaire = api.postAnswer(
                    questionnaireId,
                    questionId,
                    apiAnswer
                ).bind()

                val (_, problems) = api.getQuestionnaire(questionnaireId).bind()

                QuizData(
                    questionnaire = questionnaire,
                    problems = problems
                )
            }
        }
}