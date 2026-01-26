package com.aivanovski.leetcode.android.presentation.problemDetails

import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.model.ProblemDescriptionCellModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.model.ProblemHeaderCellModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.model.ProblemHintsCellModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel.ProblemDescriptionCellViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel.ProblemHeaderCellViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel.ProblemHintsCellViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsData

class ProblemDetailsCellFactory {

    fun createCells(data: ProblemDetailsData): List<CellViewModel> {
        val problem = data.problem

        return buildList {
            add(createHeaderCell(data))
            add(createDescriptionCell(data))

            if (problem.hints.isNotEmpty()) {
                add(createHintsCell(data))
            }
        }
    }

    private fun createHeaderCell(data: ProblemDetailsData): ProblemHeaderCellViewModel {
        val problem = data.problem

        return ProblemHeaderCellViewModel(
            model = ProblemHeaderCellModel(
                id = "header-${problem.id}",
                problemId = problem.id,
                number = "#${problem.id}",
                title = problem.title,
                categoryTitle = problem.categoryTitle,
                difficulty = problem.difficulty
            )
        )
    }

    private fun createDescriptionCell(data: ProblemDetailsData): ProblemDescriptionCellViewModel {
        val problem = data.problem

        return ProblemDescriptionCellViewModel(
            model = ProblemDescriptionCellModel(
                id = "description-${problem.id}",
                htmlContent = data.htmlContent
            )
        )
    }

    private fun createHintsCell(data: ProblemDetailsData): ProblemHintsCellViewModel {
        val problem = data.problem

        return ProblemHintsCellViewModel(
            model = ProblemHintsCellModel(
                id = "hints-${problem.id}",
                hints = problem.hints
            )
        )
    }
}