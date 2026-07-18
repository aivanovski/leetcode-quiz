package com.github.ai.leetcodequiz.api

object ProtobufResponseMapper {

    @Suppress("UNCHECKED_CAST")
    inline fun <reified Resp> getResponseBody(response: ResponseDto): Resp =
        when (Resp::class) {
            GetProblemResponse::class -> response.problemResponse
            GetProblemsResponse::class -> response.problemsResponse
            GetQuestionnaireResponse::class -> response.questionnaireResponse
            GetQuestionnairesResponse::class -> response.questionnairesResponse
            GetQuestionsResponse::class -> response.questionsResponse
            GetUnansweredQuestionsResponse::class -> response.unansweredQuestionsResponse
            PostSubmissionResponse::class -> response.submissionResponse
            LoginResponse::class -> response.loginResponse
            SignupResponse::class -> response.signupResponse
            else -> error("Unsupported protobuf response type: ${Resp::class.qualifiedName}")
        } as Resp
}