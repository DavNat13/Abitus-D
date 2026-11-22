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

/** One scrollable row: the filters button, then each active filter as a removable chip. */
@Composable
fun LibraryFilterBar(
    state: LibraryState,
    profile: EquipmentProfile,
    onOpenFilters: () -> Unit,
    onFilters: ((LibraryFilters) -> LibraryFilters) -> Unit,
    onProfile: (EquipmentProfile) -> Unit,
) {
    val f = state.filters
    val count = f.activeCount + if (profile == EquipmentProfile.ALL) 0 else 1
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = AppSpacing.screenMargin),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    ) {
        val label =
            if (count ==
                0
            ) {
                stringResource(R.string.library_filters)
            } else {
                stringResource(R.string.library_filters_count, count)
            }
        AppChip(label, selected = false, onClick = onOpenFilters, leadingIcon = AppIcons.FilterList)
        if (profile != EquipmentProfile.ALL) {
            ActiveChip(stringResource(profile.label)) { onProfile(EquipmentProfile.ALL) }
        }
        state.disciplines.firstOrNull { it.id == f.disciplineId }?.let {
            ActiveChip(it.name) {
                onFilters { s ->
                    s.copy(disciplineId = null)
                }
            }
        }
        state.muscles.firstOrNull { it.id == f.muscleId }?.let {
            ActiveChip(it.name) {
                onFilters { s ->
                    s.copy(muscleId = null)
                }
            }
        }
        state.equipment.firstOrNull { it.id == f.equipmentId }?.let {
            ActiveChip(it.name) {
                onFilters { s ->
                    s.copy(equipmentId = null)
                }
            }
        }
        f.difficulty?.let {
            ActiveChip(stringResource(R.string.difficulty_value, it)) {
                onFilters { s ->
                    s.copy(difficulty = null)
                }
            }
        }
    }
}

@Composable
private fun ActiveChip(
    label: String,
    onRemove: () -> Unit,
) {
    AppChip(label, selected = true, onClick = onRemove, leadingIcon = AppIcons.Close)
}
