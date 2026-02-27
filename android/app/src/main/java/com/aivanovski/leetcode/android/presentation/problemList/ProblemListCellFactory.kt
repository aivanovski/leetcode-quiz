package com.aivanovski.leetcode.android.presentation.problemList

import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.SpaceCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.SpaceCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.theme.QuarterMargin
import com.aivanovski.leetcode.android.presentation.problemList.cells.model.ProblemCellModel
import com.aivanovski.leetcode.android.presentation.problemList.cells.viewModel.ProblemCellViewModel

class ProblemListCellFactory {

    fun createProblemCells(
        problems: List<Problem>,
        eventProvider: CellEventProvider
    ): List<CellViewModel> {
        val cells = mutableListOf<CellViewModel>()

        cells.add(
            SpaceCellViewModel(
                model = SpaceCellModel(
                    id = "space_top",
                    height = QuarterMargin
                )
            )
        )

        for ((index, problem) in problems.withIndex()) {
            cells.add(
                ProblemCellViewModel(
                    model = ProblemCellModel(
                        id = problem.id.toString(),
                        problemId = problem.id,
                        number = "#${problem.id}",
                        title = problem.title,
                        categoryTitle = problem.categoryTitle,
                        difficulty = problem.difficulty,
                        likes = "TODO",
                        acceptanceRate = "TODO", // TODO: implement
                        submissions = "TODO" // TODO: implement
                    ),
                    eventProvider = eventProvider
                )
            )

            if (index != problems.lastIndex) {
                cells.add(
                    SpaceCellViewModel(
                        model = SpaceCellModel(
                            id = "space_$index",
                            height = QuarterMargin
                        )
                    )
                )
            }
        }

        cells.add(
            SpaceCellViewModel(
                model = SpaceCellModel(
                    id = "space_bottom",
                    height = QuarterMargin
                )
            )
        )

        return cells
    }

    private fun formatLikes(likes: Long): String {
        return when {
            likes >= 1_000_000 -> String.format("%.1fM", likes / 1_000_000.0)
            likes >= 1_000 -> String.format("%.1fK", likes / 1_000.0)
            else -> likes.toString()
        }
    }
}