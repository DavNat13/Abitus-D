package com.abitusd.app.feature.library.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.library.ui.LibraryLabels

@Composable
fun ExerciseDetailScreen(
    id: String,
    onBack: () -> Unit,
    onOpenExercise: (String) -> Unit,
    onEdit: (String) -> Unit,
    viewModel: ExerciseDetailViewModel =
        hiltViewModel<ExerciseDetailViewModel, ExerciseDetailViewModel.Factory>(
            key = id,
            creationCallback = { it.create(id) },
        ),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(viewModel) { viewModel.closed.collect { onBack() } }
    val exercise = state.exercise
    val texts = state.texts
    if (state.loading) return
    if (exercise == null || texts == null) {
        FormScreen(title = stringResource(R.string.detail_not_found), onBack = onBack) {}
        return
    }
    FormScreen(title = texts.name, subtitle = state.discipline, onBack = onBack) {
        Text(
            text =
                listOf(
                    stringResource(R.string.difficulty_value, exercise.difficulty),
                    stringResource(LibraryLabels.metric(exercise.metricType)),
                    stringResource(LibraryLabels.pattern(exercise.movementPattern)),
                ).joinToString("  |  "),
            style = AppTheme.typography.caption,
            color = AppTheme.colors.textSecondary,
        )
        DetailParagraph(if (exercise.isOwn) R.string.detail_notes else R.string.detail_summary, texts.summary)
        DetailList(R.string.detail_primary_muscles, state.primaryMuscles, numbered = false)
        DetailList(R.string.detail_secondary_muscles, state.secondaryMuscles, numbered = false)
        DetailList(R.string.detail_equipment, state.equipment, numbered = false)
        DetailParagraph(R.string.detail_setup, texts.setup)
        DetailList(R.string.detail_execution, texts.execution, numbered = true)
        DetailList(R.string.detail_cues, texts.cues, numbered = false)
        DetailList(R.string.detail_mistakes, texts.mistakes, numbered = false)
        DetailParagraph(R.string.detail_safety, texts.safety)
        RelatedExercises(R.string.detail_regressions, state.regressions, onOpenExercise)
        RelatedExercises(R.string.detail_progressions, state.progressions, onOpenExercise)
        RelatedExercises(R.string.detail_alternatives, state.alternatives, onOpenExercise)
        if (exercise.source != null && exercise.license != null) {
            Text(
                text = stringResource(R.string.detail_source, exercise.source, exercise.license),
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
        }
        if (exercise.isOwn) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                AppButton(
                    stringResource(R.string.detail_edit),
                    { onEdit(id) },
                    Modifier.weight(1f),
                    style = AppButtonStyle.Secondary,
                )
                AppButton(
                    stringResource(R.string.detail_delete),
                    { confirmDelete = true },
                    Modifier.weight(1f),
                    style = AppButtonStyle.Text,
                )
            }
        }
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.detail_delete_title)) },
            text = { Text(stringResource(R.string.detail_delete_text)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    viewModel.delete()
                }) { Text(stringResource(R.string.detail_delete_confirm), color = AppTheme.colors.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text(stringResource(R.string.detail_cancel)) }
            },
        )
    }
}
