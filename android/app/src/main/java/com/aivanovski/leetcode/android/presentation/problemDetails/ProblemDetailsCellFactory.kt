package com.aivanovski.leetcode.android.presentation.problemDetails

import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.entity.Problem
import com.aivanovski.leetcode.android.presentation.core.compose.CornersShape
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.ShapedSpaceCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.ShapedTextCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.SpaceCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.ShapedSpaceCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.ShapedTextCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.SpaceCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ElementMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.GroupMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ThemeProvider
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.model.ProblemDescriptionCellModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.model.ProblemHeaderCellModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.model.ProblemHintsCellModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel.ProblemDescriptionCellViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel.ProblemHeaderCellViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.cells.viewModel.ProblemHintsCellViewModel
import com.aivanovski.leetcode.android.presentation.problemDetails.model.ProblemDetailsData

class ProblemDetailsCellFactory(
    private val themeProvider: ThemeProvider,
    private val resources: ResourceProvider
) {

    fun createCells(data: ProblemDetailsData): List<CellViewModel> {
        val problem = data.problem

        return buildList {
            addAll(createHeaderCells(problem))
            addAll(createDescriptionCells(data))

            if (problem.hints.isNotEmpty()) {
                addAll(createHintsCells(problem))
            }

            if (problem.questions.isNotEmpty()) {
                addAll(createAlgorithmCells(problem))
            }

            if (problem.solutions.isNotEmpty()) {
                addAll(createSolutionsCells(problem))
            }

            add(createBottomSpaceCell())
        }
    }

    private fun createBottomSpaceCell(): CellViewModel {
        return SpaceCellViewModel(
            model = SpaceCellModel(
                id = "space_bottom",
                height = ElementMargin
            )
        )
    }

    private fun createHeaderCells(problem: Problem): List<CellViewModel> {
        return listOf(
            SpaceCellViewModel(
                model = SpaceCellModel(
                    id = "space_header",
                    height = ElementMargin
                )
            ),
            ProblemHeaderCellViewModel(
                model = ProblemHeaderCellModel(
                    id = "header-${problem.id}",
                    problemId = problem.id,
                    number = "#${problem.id}",
                    title = problem.title,
                    categoryTitle = problem.categoryTitle,
                    difficulty = problem.difficulty
                )
            )
        )
    }

    private fun createDescriptionCells(data: ProblemDetailsData): List<CellViewModel> {
        return listOf(
            SpaceCellViewModel(
                model = SpaceCellModel(
                    id = "space_description",
                    height = ElementMargin
                )
            ),
            ProblemDescriptionCellViewModel(
                model = ProblemDescriptionCellModel(
                    id = "description-${data.problem.id}",
                    htmlContent = data.htmlContent
                )
            )
        )
    }

    private fun createHintsCells(problem: Problem): List<CellViewModel> {
        return listOf(
            SpaceCellViewModel(
                model = SpaceCellModel(
                    id = "space_hints",
                    height = ElementMargin
                )
            ),
            ProblemHintsCellViewModel(
                model = ProblemHintsCellModel(
                    id = "hints-${problem.id}",
                    hints = problem.hints
                )
            )
        )
    }

    private fun createAlgorithmCells(problem: Problem): List<CellViewModel> {
        return buildList {
            add(
                SpaceCellViewModel(
                    model = SpaceCellModel(
                        id = "space_algorithm",
                        height = ElementMargin
                    )
                )
            )

            add(
                ShapedSpaceCellViewModel(
                    model = ShapedSpaceCellModel(
                        id = "algorithm_top",
                        height = GroupMargin,
                        shape = CornersShape.TOP
                    )
                )
            )

            add(
                ShapedTextCellViewModel(
                    model = ShapedTextCellModel(
                        id = "algorithm_header",
                        text = resources.getString(R.string.algorithm),
                        textSize = TextSize.TITLE_LARGE,
                        textColor = themeProvider.theme.colors.primaryText,
                        shape = CornersShape.NONE
                    )
                )
            )

            for ((index, question) in problem.questions.withIndex()) {
                add(
                    ShapedTextCellViewModel(
                        model = ShapedTextCellModel(
                            id = "algorithm_content_$index",
                            text = question.question,
                            textSize = TextSize.BODY_MEDIUM,
                            textColor = themeProvider.theme.colors.primaryText,
                            shape = CornersShape.NONE
                        )
                    )
                )
            }

            add(
                ShapedSpaceCellViewModel(
                    model = ShapedSpaceCellModel(
                        id = "algorithm_shape_bottom",
                        height = GroupMargin,
                        shape = CornersShape.BOTTOM
                    )
                )
            )
        }
    }

    private fun createSolutionsCells(problem: Problem): List<CellViewModel> {
        return buildList {
            add(
                SpaceCellViewModel(
                    model = SpaceCellModel(
                        id = "space_solutions",
                        height = ElementMargin
                    )
                )
            )

            add(
                ShapedSpaceCellViewModel(
                    model = ShapedSpaceCellModel(
                        id = "solutions_shape_top",
                        height = GroupMargin,
                        shape = CornersShape.TOP
                    )
                )
            )

            add(
                ShapedTextCellViewModel(
                    model = ShapedTextCellModel(
                        id = "solutions_header",
                        text = resources.getString(R.string.solutions),
                        textSize = TextSize.TITLE_LARGE,
                        textColor = themeProvider.theme.colors.primaryText,
                        shape = CornersShape.NONE
                    )
                )
            )

            for ((index, solution) in problem.solutions.withIndex()) {
                add(
                    ShapedSpaceCellViewModel(
                        model = ShapedSpaceCellModel(
                            id = "solution_space_$index",
                            height = ElementMargin,
                            shape = CornersShape.NONE
                        )
                    )
                )

                add(
                    ShapedTextCellViewModel(
                        model = ShapedTextCellModel(
                            id = "solution_content_header_$index",
                            text = resources.getString(R.string.solution_with_str, index + 1),
                            textSize = TextSize.TITLE_MEDIUM,
                            textColor = themeProvider.theme.colors.primaryText,
                            shape = CornersShape.NONE
                        )
                    )
                )

                add(
                    ShapedTextCellViewModel(
                        model = ShapedTextCellModel(
                            id = "solution_content_$index",
                            text = solution,
                            textSize = TextSize.BODY_MEDIUM,
                            textColor = themeProvider.theme.colors.primaryText,
                            shape = CornersShape.NONE
                        )
                    )
                )
            }

            add(
                ShapedSpaceCellViewModel(
                    model = ShapedSpaceCellModel(
                        id = "solutions_shape_bottom",
                        height = GroupMargin,
                        shape = CornersShape.BOTTOM
                    )
                )
            )
        }
    }
}