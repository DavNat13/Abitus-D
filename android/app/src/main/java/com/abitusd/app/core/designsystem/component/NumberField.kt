package com.abitusd.app.core.designsystem.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Small numeric field; empty means "not set". [compact] drops the floating label (column headers). */
@Composable
fun NumberField(
    value: Number?,
    label: String,
    onValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    decimal: Boolean = false,
    compact: Boolean = false,
) {
    var text by remember(value) { mutableStateOf(value?.let { formatNumber(it) }.orEmpty()) }
    OutlinedTextField(
        value = text,
        onValueChange = { input ->
            val clean = input.replace(',', '.').filter { it.isDigit() || (decimal && it == '.') }.take(MAX_CHARS)
            text = clean
            onValue(clean)
        },
        label = if (compact) null else ({ Text(label, style = AppTheme.typography.caption) }),
        placeholder = if (compact) ({ Text(label, style = AppTheme.typography.caption, maxLines = 1) }) else null,
        singleLine = true,
        shape = AppShapes.field,
        textStyle = AppTheme.typography.body,
        keyboardOptions = KeyboardOptions(keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AppTheme.colors.surfaceSoft,
                unfocusedContainerColor = AppTheme.colors.surfaceSoft,
                focusedBorderColor = AppTheme.colors.primary,
                unfocusedBorderColor = AppTheme.colors.inputBorder,
            ),
        modifier = modifier,
    )
}

private const val MAX_CHARS = 7

private fun formatNumber(value: Number): String =
    if (value is Double && value % 1.0 != 0.0) value.toString() else value.toLong().toString()
