package com.aivanovski.leetcode.android.presentation.quiz

import com.aivanovski.leetcode.android.domain.ProblemHtmlFormatter
import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.entity.Question
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider
import com.aivanovski.leetcode.android.presentation.quiz.cells.model.QuestionCardCellModel
import com.aivanovski.leetcode.android.presentation.quiz.cells.viewModel.QuestionCardCellViewModel

class QuizCellFactory(
    private val htmlFormatter: ProblemHtmlFormatter,
    private val resources: ResourceProvider
) {

    fun createQuestionCell(
        question: Question,
        problemIdToProblemMap: Map<Int, Problem>,
        eventProvider: CellEventProvider
    ): QuestionCardCellViewModel? {
        val problem = problemIdToProblemMap[question.problemId] ?: return null

        return QuestionCardCellViewModel(
            model = QuestionCardCellModel(
                id = question.id,
                number = "#${question.problemId}",
                title = problem.title,
                frontHtmlContent = htmlFormatter.formatProblemHtml(problem.content),
                backContent = question.question
            ),
            eventProvider = eventProvider
        )
    }
}