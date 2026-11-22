package com.abitusd.app.feature.library.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.catalog.NamedRef
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppChip
import com.abitusd.app.core.designsystem.component.SectionHeader
import com.abitusd.app.core.designsystem.icon.DisciplineIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.feature.library.domain.EquipmentProfile
import com.abitusd.app.feature.library.ui.LibraryLabels

/** All library filters in one sheet; changes apply live and the count shows on the main button. */
@Composable
fun LibraryFilterSheet(
    state: LibraryState,
    profile: EquipmentProfile,
    onFilters: ((LibraryFilters) -> LibraryFilters) -> Unit,
    onProfile: (EquipmentProfile) -> Unit,
    onDismiss: () -> Unit,
) {
    val f = state.filters
    AppBottomSheet(stringResource(R.string.library_filters), onDismiss) {
        SectionHeader(stringResource(R.string.profile_equipment))
        ChipFlow {
            EquipmentProfile.entries.forEach { p -> AppChip(stringResource(p.label), p == profile, { onProfile(p) }) }
        }
        SectionHeader(stringResource(R.string.library_filter_discipline))
        ChipFlow {
            state.disciplines.forEach { d ->
                val selected = d.id == f.disciplineId
                AppChip(d.name, selected, {
                    onFilters { it.copy(disciplineId = toggle(selected, d.id)) }
                }, leadingIcon = DisciplineIcons.of(state.icons[d.id]))
            }
        }
        SectionHeader(stringResource(R.string.library_filter_difficulty))
        ChipFlow {
            LibraryLabels.DIFFICULTIES.forEach { level ->
                val selected = level == f.difficulty
                AppChip(level.toString(), selected, { onFilters { it.copy(difficulty = toggle(selected, level)) } })
            }
        }
        SectionHeader(stringResource(R.string.library_filter_muscle))
        RefChips(state.muscles, f.muscleId) { id -> onFilters { it.copy(muscleId = id) } }
        SectionHeader(stringResource(R.string.library_filter_equipment))
        RefChips(state.equipment, f.equipmentId) { id -> onFilters { it.copy(equipmentId = id) } }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            AppButton(
                stringResource(R.string.library_filters_clear),
                {
                    onFilters { LibraryFilters() }
                    onProfile(EquipmentProfile.ALL)
                },
                Modifier.weight(1f),
                AppButtonStyle.Secondary,
            )
            AppButton(stringResource(R.string.library_show_results, state.items.size), onDismiss, Modifier.weight(1f))
        }
    }
}

@Composable
private fun RefChips(
    refs: List<NamedRef>,
    selectedId: Int?,
    onSelect: (Int?) -> Unit,
) {
    ChipFlow {
        refs.forEach { ref ->
            val selected = ref.id == selectedId
            AppChip(ref.name, selected, { onSelect(toggle(selected, ref.id)) })
        }
    }
}

@Composable
private fun ChipFlow(content: @Composable () -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) { content() }
}

private fun toggle(
    selected: Boolean,
    id: Int,
): Int? = if (selected) null else id
