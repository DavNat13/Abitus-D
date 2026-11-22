package com.abitusd.app.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Field of 05c: soft surface, input border, 2 dp focus, error message below. */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    supportingText: String? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    enabled: Boolean = true,
) {
    val colors = AppTheme.colors
    var visible by rememberSaveable { mutableStateOf(false) }
    val helper = error ?: supportingText
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        label = { Text(label, style = AppTheme.typography.label) },
        textStyle = AppTheme.typography.body,
        singleLine = true,
        isError = error != null,
        shape = AppShapes.field,
        supportingText = helper?.let { { Text(it, style = AppTheme.typography.caption) } },
        visualTransformation =
            if (isPassword && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions =
            KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
                imeAction = imeAction,
                autoCorrectEnabled = false,
            ),
        keyboardActions = KeyboardActions(onAny = { onImeAction() }),
        trailingIcon =
            if (isPassword) {
                {
                    IconButton(onClick = { visible = !visible }) {
                        Icon(
                            painter = painterResource(if (visible) AppIcons.HidePassword else AppIcons.ShowPassword),
                            contentDescription =
                                stringResource(
                                    if (visible) R.string.common_hide_password else R.string.common_show_password,
                                ),
                            tint = colors.textSecondary,
                        )
                    }
                }
            } else {
                null
            },
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colors.surfaceSoft,
                unfocusedContainerColor = colors.surfaceSoft,
                disabledContainerColor = colors.surfaceSoft,
                errorContainerColor = colors.surfaceSoft,
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.inputBorder,
                errorBorderColor = colors.error,
                focusedLabelColor = colors.primary,
                unfocusedLabelColor = colors.textSecondary,
                errorLabelColor = colors.error,
                errorSupportingTextColor = colors.error,
                unfocusedSupportingTextColor = colors.textSecondary,
                focusedSupportingTextColor = colors.textSecondary,
                cursorColor = colors.primary,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
            ),
    )
}
