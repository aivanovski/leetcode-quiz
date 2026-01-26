package com.aivanovski.leetcode.android.data.api

class ApiUrlBuilder(
    private val baseUrl: String
) {
    fun login(): String = "$baseUrl/api/login"
    fun problems(): String = "$baseUrl/api/problem"
    fun problem(problemId: String): String = "$baseUrl/api/problem/$problemId"
    fun questionnaires(): String = "$baseUrl/api/questionnaire"
    fun questionnaire(questionnaireId: String) = "$baseUrl/api/questionnaire/$questionnaireId"
}