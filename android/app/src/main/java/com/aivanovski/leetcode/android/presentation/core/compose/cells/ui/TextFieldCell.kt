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
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextFieldCellEvent.OnIconClick
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextFieldCellEvent.OnTextChange
import com.aivanovski.leetcode.android.presentation.core.compose.cells.model.TextFieldCellModel
import com.aivanovski.leetcode.android.presentation.core.compose.cells.viewModel.TextFieldCellViewModel
import com.aivanovski.leetcode.android.presentation.core.compose.preview.PreviewEventProvider
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.ElementMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme

@Composable
fun TextFieldCell(viewModel: TextFieldCellViewModel) {
    val model by viewModel.observableModel.collectAsStateWithLifecycle()

    AppTextField(
        value = model.value,
        label = model.label,
        icon = model.icon,
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
fun TextFieldCellPreview() {
    ThemedPreview(LightTheme) {
        Column {
            TextFieldCell(newTextFieldCell())
        }
    }
}

fun newTextFieldCell() =
    TextFieldCellViewModel(
        initialModel = TextFieldCellModel(
            id = "id",
            value = "admin",
            label = "Username",
            icon = null
        ),
        eventProvider = PreviewEventProvider
    )