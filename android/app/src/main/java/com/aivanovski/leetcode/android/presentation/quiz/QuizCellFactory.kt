package com.aivanovski.leetcode.android.presentation.quiz

import com.aivanovski.leetcode.android.domain.ProblemHtmlFormatter
import com.aivanovski.leetcode.android.entity.ProblemWithContent
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
        problem: ProblemWithContent,
        eventProvider: CellEventProvider
    ): QuestionCardCellViewModel {
        return QuestionCardCellViewModel(
            model = QuestionCardCellModel(
                id = question.uid,
                number = "#${question.problemId}",
                title = problem.problem.title,
                frontHtmlContent = htmlFormatter.formatProblemHtml(problem.content),
                backContent = question.question
            ),
            eventProvider = eventProvider
        )
    }
}