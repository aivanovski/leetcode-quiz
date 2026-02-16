package com.aivanovski.leetcode.android.presentation.core.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aivanovski.leetcode.android.presentation.core.compose.icons.VectorIcon
import com.aivanovski.leetcode.android.presentation.core.compose.preview.ThemedPreview
import com.aivanovski.leetcode.android.presentation.core.compose.theme.HalfMargin
import com.aivanovski.leetcode.android.presentation.core.compose.theme.LightTheme
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun AppTextField(
    value: String,
    label: String,
    error: String? = null,
    onValueChange: (String) -> Unit,
    isTextHidden: Boolean = false,
    icon: VectorIcon? = null,
    onIconClick: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    // TODO: resolve issue with inner state
    val innerValue = remember {
        MutableStateFlow(value)
    }
    innerValue.value = value

    val onChange = rememberCallback { newValue: String ->
        innerValue.value = newValue
        onValueChange.invoke(newValue)
    }

    val observedInnerValue by innerValue.collectAsState()

    val isError = (error != null)

    OutlinedTextField(
        value = observedInnerValue,
        onValueChange = onChange,
        label = {
            Text(label)
        },
        isError = isError,
        visualTransformation = if (isTextHidden) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        supportingText = {
            if (isError) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        trailingIcon = {
            when {
                icon != null -> {
                    Icon(
                        imageVector = icon.vector,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(),
                                onClick = {
                                    onIconClick?.invoke()
                                }
                            )
                            .padding(HalfMargin)
                    )
                }

                isError -> {
                    Icon(
                        imageVector = VectorIcon.ERROR_CIRCLE.vector,
                        contentDescription = null
                    )
                }
            }
        },
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )
}

@Composable
@Preview
fun TextFieldsLightPreview() {
    ThemedPreview(theme = LightTheme) {
        Column {
            AppTextField(
                value = "",
                label = "Username",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            AppTextField(
                value = "john.doe",
                label = "Username",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            AppTextField(
                value = "abc123",
                label = "Password",
                onValueChange = {},
                isTextHidden = false,
                icon = VectorIcon.VISIBILITY_ON,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            AppTextField(
                value = "abc123",
                label = "Password",
                onValueChange = {},
                isTextHidden = true,
                icon = VectorIcon.VISIBILITY_OFF,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            AppTextField(
                value = "john.doe",
                label = "Username",
                error = "username is already exists",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            AppTextField(
                value = "john.doe",
                label = "Username",
                icon = VectorIcon.CLOSE,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}