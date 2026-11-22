package com.abitusd.app.feature.library.ui.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.abitusd.app.R
import com.abitusd.app.core.database.catalog.ExerciseListRow
import com.abitusd.app.core.database.catalog.NamedRef
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.MenuChip
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.library.ui.LibraryLabels

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
                    Icon(painterResource(AppIcons.Close), stringResource(R.string.library_clear_search))
                }
            }
        },
        singleLine = true,
        shape = AppShapes.field,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colors.surfaceSoft,
                unfocusedContainerColor = colors.surfaceSoft,
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.inputBorder,
            ),
    )
}

@Composable
fun LibraryFilterRow(
    state: LibraryState,
    onFilters: ((LibraryFilters) -> LibraryFilters) -> Unit,
) {
    val any = stringResource(R.string.library_filter_any)
    val f = state.filters
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = AppSpacing.screenMargin, vertical = AppSpacing.xs),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    ) {
        MenuChip(
            stringResource(R.string.library_filter_discipline),
            any,
            f.disciplineId,
            state.disciplines.options(),
        ) { value ->
            onFilters { it.copy(disciplineId = value) }
        }
        MenuChip(stringResource(R.string.library_filter_muscle), any, f.muscleId, state.muscles.options()) { value ->
            onFilters { it.copy(muscleId = value) }
        }
        MenuChip(
            stringResource(R.string.library_filter_equipment),
            any,
            f.equipmentId,
            state.equipment.options(),
        ) { value ->
            onFilters { it.copy(equipmentId = value) }
        }
        val levels = LibraryLabels.DIFFICULTIES.map { ChoiceOption(it, stringResource(R.string.difficulty_value, it)) }
        MenuChip(stringResource(R.string.library_filter_difficulty), any, f.difficulty, levels) { value ->
            onFilters { it.copy(difficulty = value) }
        }
    }
}

private fun List<NamedRef>.options() = map { ChoiceOption(it.id, it.name) }

@Composable
fun ExerciseRowCard(
    row: ExerciseListRow,
    disciplines: List<NamedRef>,
    onClick: () -> Unit,
) {
    val colors = AppTheme.colors
    Card(
        onClick = onClick,
        shape = AppShapes.card,
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(AppSizes.borderThin, colors.outline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(AppSpacing.md), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs)) {
                Text(row.name, style = AppTheme.typography.titleMedium, color = colors.textPrimary)
                Text(
                    text = disciplines.firstOrNull { it.id == row.disciplineId }?.name.orEmpty(),
                    style = AppTheme.typography.caption,
                    color = colors.textSecondary,
                )
            }
            if (row.isOwn) {
                Text(
                    stringResource(R.string.library_own_tag),
                    style = AppTheme.typography.caption,
                    color = colors.primary,
                )
            }
        }
    }
}
