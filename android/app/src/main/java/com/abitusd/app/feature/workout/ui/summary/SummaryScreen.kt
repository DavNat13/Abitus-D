package com.abitusd.app.feature.workout.ui.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.ConfirmDialog
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.workout.ui.active.formatKg
import com.abitusd.app.feature.workout.ui.active.setValue

/** End of the session (10f) or, with [finished], a past session from history. */
@Composable
fun SummaryScreen(
    sessionId: String,
    finished: Boolean,
    onBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: SummaryViewModel =
        hiltViewModel<SummaryViewModel, SummaryViewModel.Factory>(key = "summary-$sessionId", creationCallback = {
            it.create(sessionId)
        }),
) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    var confirmDiscard by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(viewModel) { viewModel.done.collect { onDone() } }
    val summary = s.summary
    FormScreen(
        title = if (finished) s.title else stringResource(R.string.summary_title),
        subtitle = if (finished) null else s.title,
        onBack = onBack,
    ) {
        if (summary != null) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                InfoPill(
                    stringResource(R.string.summary_duration_value, summary.durationMin.toInt()),
                    emphasized = true,
                )
                InfoPill(stringResource(R.string.summary_volume_value, formatKg(summary.volumeKg)))
                InfoPill(stringResource(R.string.summary_sets_value, summary.sets))
            }
            SectionCard(stringResource(R.string.summary_prs), icon = AppIcons.Progression) {
                if (summary.records.isEmpty()) {
                    Text(
                        stringResource(R.string.summary_no_prs),
                        style = AppTheme.typography.body,
                        color = AppTheme.colors.textSecondary,
                    )
                }
                summary.records.forEach { pr ->
                    InfoPill(
                        stringResource(
                            R.string.summary_pr_item,
                            s.names[pr.exerciseId].orEmpty(),
                            formatKg(pr.loadKg),
                            pr.reps,
                        ),
                        emphasized = true,
                    )
                }
            }
        }
        s.exercises.filter { it.sets.isNotEmpty() }.forEach { exercise ->
            SectionCard(exercise.name, icon = AppIcons.Train) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                ) {
                    exercise.sets.forEach { InfoPill(setValue(it)) }
                }
            }
        }
        if (!finished) {
            SectionCard(stringResource(R.string.summary_notes), icon = AppIcons.Notes) {
                val rpe = (1..MAX_RPE).map { ChoiceOption(it, it.toString()) }
                AppDropdown(
                    stringResource(R.string.summary_rpe),
                    rpe,
                    s.rpe,
                    viewModel::setRpe,
                    noneLabel = stringResource(R.string.library_filter_any),
                )
                AppTextField(s.notes, viewModel::setNotes, stringResource(R.string.summary_notes))
            }
            if (s.templateSaved) StatusBanner(stringResource(R.string.summary_template_saved), StatusKind.Success)
            AppButton(stringResource(R.string.summary_save), viewModel::save, icon = AppIcons.Check)
            AppButton(
                stringResource(R.string.summary_save_template),
                viewModel::saveAsTemplate,
                style = AppButtonStyle.Tonal,
                icon = AppIcons.Duplicate,
            )
            AppButton(stringResource(R.string.workout_discard), {
                confirmDiscard = true
            }, style = AppButtonStyle.Destructive, icon = AppIcons.Delete)
        }
    }
    if (confirmDiscard) {
        ConfirmDialog(
            title = stringResource(R.string.workout_discard_title),
            message = stringResource(R.string.workout_discard_text),
            confirmLabel = stringResource(R.string.workout_discard),
            dismissLabel = stringResource(R.string.detail_cancel),
            onConfirm = viewModel::discard,
            onDismiss = { confirmDiscard = false },
        )
    }
}

private const val MAX_RPE = 10
