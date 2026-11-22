package com.abitusd.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

const val CODE_LENGTH = 6

/** Six boxes backed by one numeric field, so paste and the keyboard work naturally. */
@Composable
fun CodeInput(
    code: String,
    onCodeChange: (String) -> Unit,
    accessibilityLabel: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    onDone: () -> Unit = {},
) {
    val colors = AppTheme.colors
    BasicTextField(
        value = code,
        onValueChange = { input -> onCodeChange(input.filter(Char::isDigit).take(CODE_LENGTH)) },
        modifier = modifier.semantics { contentDescription = accessibilityLabel },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs, Alignment.CenterHorizontally),
            ) {
                repeat(CODE_LENGTH) { index ->
                    val focused = index == code.length
                    val borderColor =
                        when {
                            isError -> colors.error
                            focused -> colors.primary
                            else -> colors.inputBorder
                        }
                    Box(
                        modifier =
                            Modifier
                                .size(AppSizes.codeBox)
                                .background(colors.surfaceSoft, AppShapes.field)
                                .border(
                                    if (focused) AppSizes.borderFocus else AppSizes.borderThin,
                                    borderColor,
                                    AppShapes.field,
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = code.getOrNull(index)?.toString().orEmpty(),
                            style = AppTheme.typography.titleLarge,
                            color = colors.textPrimary,
                        )
                    }
                }
            }
        },
    )
}
