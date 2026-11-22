package com.abitusd.app.feature.train.ui.routines

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Superset letter: one letter, upper case; empty means no superset. */
@Composable
fun GroupField(
    value: String,
    onValue: (String) -> Unit,
    modifier: Modifier,
) {
    val colors = AppTheme.colors
    OutlinedTextField(
        value = value,
        onValueChange = { onValue(it.filter(Char::isLetter).take(1).uppercase()) },
        label = { Text(stringResource(R.string.routine_group), style = AppTheme.typography.caption) },
        singleLine = true,
        shape = AppShapes.field,
        textStyle = AppTheme.typography.body,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colors.surfaceSoft,
                unfocusedContainerColor = colors.surfaceSoft,
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.inputBorder,
            ),
        modifier = modifier,
    )
}
