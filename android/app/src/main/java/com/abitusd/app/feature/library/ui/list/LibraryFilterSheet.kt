package com.abitusd.app.feature.library.ui.list

import androidx.compose.foundation.layout.Arrangement
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
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.feature.library.domain.EquipmentProfile
import com.abitusd.app.feature.library.ui.LibraryLabels

/** All library filters as compact dropdowns in one sheet; changes apply live. */
@Composable
fun LibraryFilterSheet(
    state: LibraryState,
    profile: EquipmentProfile,
    onFilters: ((LibraryFilters) -> LibraryFilters) -> Unit,
    onProfile: (EquipmentProfile) -> Unit,
    onDismiss: () -> Unit,
) {
    val f = state.filters
    val any = stringResource(R.string.library_filter_any)
    AppBottomSheet(stringResource(R.string.library_filters), onDismiss) {
        val profiles = EquipmentProfile.entries.map { ChoiceOption(it, stringResource(it.label)) }
        AppDropdown(stringResource(R.string.profile_equipment), profiles, profile, {
            onProfile(it ?: EquipmentProfile.ALL)
        })
        AppDropdown(
            stringResource(
                R.string.library_filter_discipline,
            ),
            state.disciplines.options(),
            f.disciplineId,
            { v ->
                onFilters { it.copy(disciplineId = v) }
            },
            noneLabel = any,
        )
        AppDropdown(stringResource(R.string.library_filter_muscle), state.muscles.options(), f.muscleId, { v ->
            onFilters { it.copy(muscleId = v) }
        }, noneLabel = any)
        AppDropdown(stringResource(R.string.library_filter_equipment), state.equipment.options(), f.equipmentId, { v ->
            onFilters { it.copy(equipmentId = v) }
        }, noneLabel = any)
        val levels = LibraryLabels.DIFFICULTIES.map { ChoiceOption(it, stringResource(R.string.difficulty_value, it)) }
        AppDropdown(stringResource(R.string.library_filter_difficulty), levels, f.difficulty, { v ->
            onFilters { it.copy(difficulty = v) }
        }, noneLabel = any)
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

private fun List<NamedRef>.options() = map { ChoiceOption(it.id, it.name) }
