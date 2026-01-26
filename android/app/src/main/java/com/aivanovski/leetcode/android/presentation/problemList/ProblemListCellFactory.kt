package com.aivanovski.leetcode.android.presentation.problemList

import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider
import com.aivanovski.leetcode.android.presentation.problemList.cells.model.ProblemCellModel
import com.aivanovski.leetcode.android.presentation.problemList.cells.viewModel.ProblemCellViewModel

class ProblemListCellFactory(
    private val resources: ResourceProvider
) {

    fun createProblemCells(
        problems: List<Problem>,
        eventProvider: CellEventProvider
    ): List<ProblemCellViewModel> {
        return problems.map { problem ->
            ProblemCellViewModel(
                model = ProblemCellModel(
                    id = problem.id.toString(),
                    problemId = problem.id,
                    number = "#${problem.id}",
                    title = problem.title,
                    categoryTitle = problem.categoryTitle,
                    difficulty = problem.difficulty,
                    likes = formatLikes(problem.likes),
                    acceptanceRate = "TODO", // TODO: implement
                    submissions = "TODO" // TODO: implement
                ),
                eventProvider = eventProvider
            )
        }
    }

    private fun formatLikes(likes: Long): String {
        return when {
            likes >= 1_000_000 -> String.format("%.1fM", likes / 1_000_000.0)
            likes >= 1_000 -> String.format("%.1fK", likes / 1_000.0)
            else -> likes.toString()
        }
    }
}