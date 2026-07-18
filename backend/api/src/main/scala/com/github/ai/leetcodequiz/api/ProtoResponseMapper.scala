package com.github.ai.leetcodequiz.api

import scalapb.GeneratedMessage

object ProtoResponseMapper {

  def createResponseDto[ProtoResponse <: GeneratedMessage](
    message: ProtoResponse
  ): ResponseDto = {
    val (responseType, body) = message match {
      case response: GetProblemResponse =>
        ResponseType.GET_PROBLEM -> ResponseDto.Body.ProblemResponse(response)

      case response: GetProblemsResponse =>
        ResponseType.GET_PROBLEMS -> ResponseDto.Body.ProblemsResponse(response)

      case response: GetQuestionnaireResponse =>
        ResponseType.GET_QUESTIONNAIRE -> ResponseDto.Body.QuestionnaireResponse(response)

      case response: GetQuestionnairesResponse =>
        ResponseType.GET_QUESTIONNAIRES -> ResponseDto.Body.QuestionnairesResponse(response)

      case response: GetQuestionsResponse =>
        ResponseType.GET_QUESTIONS -> ResponseDto.Body.QuestionsResponse(response)

      case response: GetUnansweredQuestionsResponse =>
        ResponseType.GET_UNANSWERED_QUESTIONS -> ResponseDto.Body.UnansweredQuestionsResponse(
          response
        )

      case response: PostSubmissionResponse =>
        ResponseType.POST_SUBMISSION -> ResponseDto.Body.SubmissionResponse(response)

      case response: LoginResponse =>
        ResponseType.POST_LOGIN -> ResponseDto.Body.LoginResponse(response)

      case response: SignupResponse =>
        ResponseType.POST_SIGNUP -> ResponseDto.Body.SignupResponse(response)

      case _ =>
        ResponseType.UNDEFINED -> ResponseDto.Body.Empty
    }

    ResponseDto(
      responseType = responseType,
      errorMessageDto = None,
      body = body
    )
  }
}
