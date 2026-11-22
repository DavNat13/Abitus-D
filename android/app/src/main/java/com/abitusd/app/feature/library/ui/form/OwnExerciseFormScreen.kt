package com.abitusd.app.feature.library.ui.form

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.AppMultiDropdown
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.library.ui.LibraryLabels

@Composable
fun OwnExerciseFormScreen(
    editId: String?,
    onBack: () -> Unit,
    onSaved: (String) -> Unit,
    onOpenExisting: (String) -> Unit,
    viewModel: OwnExerciseFormViewModel =
        hiltViewModel<OwnExerciseFormViewModel, OwnExerciseFormViewModel.Factory>(
            key = editId ?: "new",
            creationCallback = { it.create(editId) },
        ),
) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.saved.collect(onSaved) }
    val title = if (s.isEdit) R.string.form_edit_title else R.string.form_new_title
    val update = viewModel::update
    FormScreen(title = stringResource(title), onBack = onBack) {
        SectionCard(stringResource(R.string.form_section_basics), icon = AppIcons.Notes) {
            AppTextField(s.name, { v -> update { it.copy(name = v) } }, stringResource(R.string.form_name))
            if (s.duplicates.isNotEmpty()) {
                StatusBanner(stringResource(R.string.form_duplicate), StatusKind.Warning)
                s.duplicates.forEach { dup ->
                    AppButton(stringResource(R.string.form_open_existing, dup.name), {
                        onOpenExisting(dup.id)
                    }, style = AppButtonStyle.Text)
                }
            }
            AppTextField(
                s.notes,
                { v -> update { it.copy(notes = v.take(MAX_NOTES)) } },
                stringResource(R.string.form_notes),
            )
        }
        SectionCard(stringResource(R.string.form_section_type), icon = AppIcons.Train) {
            AppDropdown(
                stringResource(R.string.form_discipline),
                s.disciplines.map {
                    ChoiceOption(it.id, it.name)
                },
                s.disciplineId,
                { v ->
                    v?.let { value -> update { it.copy(disciplineId = value) } }
                },
            )
            val metrics = LibraryLabels.METRICS.map { (key, res) -> ChoiceOption(key, stringResource(res)) }
            AppDropdown(stringResource(R.string.form_metric), metrics, s.metricType, { v ->
                v?.let { value -> update { it.copy(metricType = value) } }
            })
            val patterns = LibraryLabels.PATTERNS.map { (key, res) -> ChoiceOption(key, stringResource(res)) }
            AppDropdown(stringResource(R.string.form_pattern), patterns, s.movementPattern, { v ->
                v?.let { value -> update { it.copy(movementPattern = value) } }
            })
            val levels =
                LibraryLabels.DIFFICULTIES.map {
                    ChoiceOption(
                        it,
                        stringResource(R.string.difficulty_value, it),
                    )
                }
            AppDropdown(stringResource(R.string.form_difficulty), levels, s.difficulty, { v ->
                v?.let { value -> update { it.copy(difficulty = value) } }
            })
        }
        SectionCard(stringResource(R.string.detail_muscles_equipment), icon = AppIcons.Muscles) {
            val muscles = s.muscles.map { ChoiceOption(it.id, it.name) }
            AppMultiDropdown(stringResource(R.string.form_primary_muscles), muscles, s.primary, { v ->
                update {
                    it.copy(primary = it.primary.toggle(v))
                }
            })
            AppMultiDropdown(stringResource(R.string.form_secondary_muscles), muscles, s.secondary, { v ->
                update { it.copy(secondary = it.secondary.toggle(v)) }
            })
            val equipment = s.equipmentOptions.map { ChoiceOption(it.id, it.name) }
            AppMultiDropdown(stringResource(R.string.form_equipment), equipment, s.equipment, { v ->
                update {
                    it.copy(equipment = it.equipment.toggle(v))
                }
            })
        }
        s.error?.let { StatusBanner(stringResource(it), StatusKind.Error) }
        Text(
            stringResource(R.string.form_saved_offline),
            style = AppTheme.typography.caption,
            color = AppTheme.colors.textSecondary,
        )
        AppButton(stringResource(R.string.form_save), viewModel::save, loading = s.saving, icon = AppIcons.Check)
    }
}

private const val MAX_NOTES = 500

private fun Set<Int>.toggle(value: Int): Set<Int> = if (value in this) this - value else this + value
