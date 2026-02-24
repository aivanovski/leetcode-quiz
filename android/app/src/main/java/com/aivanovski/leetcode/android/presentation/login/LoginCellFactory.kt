package com.aivanovski.leetcode.android.presentation.login

import com.aivanovski.leetcode.android.R
import com.aivanovski.leetcode.android.domain.usecases.GetDebugCredentialsUseCase.DebugCredentials
import com.aivanovski.leetcode.android.presentation.core.compose.TextSize
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.cells.CellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.ButtonCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.SecretFieldCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.SpaceCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextChipItem
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextChipRowCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextFieldCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.ButtonCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.SecretFieldCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.SpaceCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.TextChipRowCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.TextFieldCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ElementMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.HalfMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ThemeProvider
import com.aivanovski.leetcode.android.presentation.core.resources.ResourceProvider

class LoginCellFactory(
    private val resources: ResourceProvider,
    private val themeProvider: ThemeProvider
) {

    fun createCells(
        debugCredentials: List<DebugCredentials>,
        eventProvider: CellEventProvider
    ): List<CellViewModel> {
        val cells = mutableListOf<CellViewModel>()

        cells.add(
            SpaceCellViewModel(
                model = SpaceCellModel(
                    id = LoginCellId.SPACE_TOP.id,
                    height = ElementMargin
                )
            )
        )

        if (debugCredentials.isNotEmpty()) {
            cells.add(
                TextChipRowCellViewModel(
                    model = TextChipRowCellModel(
                        id = LoginCellId.DEBUG_CREDENTIALS.id,
                        chips = debugCredentials.map { credentials ->
                            TextChipItem(
                                text = credentials.email,
                                textColor = themeProvider.theme.colors.primaryText,
                                textSize = TextSize.BODY_MEDIUM,
                                isClickable = true,
                                isSelected = false
                            )
                        }
                    ),
                    eventProvider = eventProvider
                )
            )
        }

        cells.add(
            SpaceCellViewModel(
                model = SpaceCellModel(
                    id = LoginCellId.SPACE_MIDDLE.id,
                    height = HalfMargin
                )
            )
        )

        cells.add(
            TextFieldCellViewModel(
                initialModel = TextFieldCellModel(
                    id = LoginCellId.EMAIL.id,
                    value = "",
                    label = resources.getString(R.string.email),
                    icon = null
                ),
                eventProvider = eventProvider
            )
        )

        cells.add(
            SpaceCellViewModel(
                model = SpaceCellModel(
                    id = LoginCellId.SPACE_MIDDLE.id,
                    height = HalfMargin
                )
            )
        )

        cells.add(
            SecretFieldCellViewModel(
                initialModel = SecretFieldCellModel(
                    id = LoginCellId.PASSWORD.id,
                    value = "",
                    label = resources.getString(R.string.password),
                    isTextVisible = false
                ),
                eventProvider = eventProvider
            )
        )

        cells.add(
            SpaceCellViewModel(
                model = SpaceCellModel(
                    id = LoginCellId.SPACE_BOTTOM.id,
                    height = ElementMargin
                )
            )
        )

        cells.add(
            ButtonCellViewModel(
                model = ButtonCellModel(
                    id = LoginCellId.LOGIN_BUTTON.id,
                    text = resources.getString(R.string.log_in),
                    buttonColor = themeProvider.theme.colors.primaryButton
                ),
                eventProvider = eventProvider
            )
        )

        return cells
    }

    enum class LoginCellId {
        EMAIL,
        PASSWORD,
        SPACE_TOP,
        SPACE_MIDDLE,
        SPACE_BOTTOM,
        DEBUG_CREDENTIALS,
        LOGIN_BUTTON;

        val id = name
    }
}