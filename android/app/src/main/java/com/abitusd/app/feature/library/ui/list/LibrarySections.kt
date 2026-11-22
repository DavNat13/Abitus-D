package com.abitusd.app.feature.library.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.abitusd.app.R
import com.abitusd.app.core.database.catalog.ExerciseListRow
import com.abitusd.app.core.designsystem.component.AppChip
import com.abitusd.app.core.designsystem.component.ListItemCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.icon.DisciplineIcons
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.library.domain.EquipmentProfile

@Composable
fun LibrarySearchField(
    query: String,
    onQuery: (String) -> Unit,
) {
    val colors = AppTheme.colors
    OutlinedTextField(
        value = query,
        onValueChange = onQuery,
        modifier = Modifier.fillMaxWidth().padding(horizontal = AppSpacing.screenMargin),
        placeholder = { Text(stringResource(R.string.library_search), style = AppTheme.typography.body) },
        leadingIcon = {
            Icon(
                painterResource(AppIcons.Search),
                contentDescription = null,
                tint = colors.textSecondary,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQuery("") }) {
                    Icon(
                        painterResource(AppIcons.Close),
                        stringResource(R.string.library_clear_search),
                        tint = colors.textSecondary,
                    )
                }
            }
        },
        singleLine = true,
        shape = AppShapes.field,
        textStyle = AppTheme.typography.body,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colors.surfaceSoft,
                unfocusedContainerColor = colors.surfaceSoft,
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.outline,
                cursorColor = colors.primary,
            ),
    )
}
