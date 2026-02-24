package com.aivanovski.leetcode.android.presentation.core.compose.cells.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aivanovski.leetcode.android.presentation.core.compose.AppTextField
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.SecretFieldCellEvent.OnIconClick
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.SecretFieldCellEvent.OnTextChange
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.SecretFieldCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.SecretFieldCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.icons.VectorIcon
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ElementSpace
import com.aivanovski.leetcode.android.presentation.core.compose.preview.PreviewEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ElementMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme

@Composable
fun SecretFieldCell(viewModel: SecretFieldCellViewModel) {
    val model by viewModel.observableModel.collectAsStateWithLifecycle()

    AppTextField(
        value = model.value,
        label = model.label,
        isTextHidden = !model.isTextVisible,
        icon = if (model.isTextVisible) {
            VectorIcon.VISIBILITY_OFF
        } else {
            VectorIcon.VISIBILITY_ON
        },
        onIconClick = {
            viewModel.sendEvent(OnIconClick(cellId = model.id))
        },
        onValueChange = { newValue ->
            viewModel.sendEvent(OnTextChange(model.id, newValue))
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ElementMargin)
    )
}

@Preview
@Composable
fun SecretFieldCellPreview() {
    ThemedPreview(LightTheme) {
        Column {
            SecretFieldCell(newSecretFieldCell(isTextVisible = true))
            ElementSpace()
            SecretFieldCell(newSecretFieldCell(isTextVisible = false))
        }
    }
}

fun newSecretFieldCell(isTextVisible: Boolean = false) =
    SecretFieldCellViewModel(
        initialModel = SecretFieldCellModel(
            id = "id",
            value = "abc123",
            label = "Password",
            isTextVisible = isTextVisible
        ),
        eventProvider = PreviewEventProvider
    )