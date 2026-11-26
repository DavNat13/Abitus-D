package com.abitusd.app.feature.library.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
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
import com.abitusd.app.core.designsystem.component.ConfirmDialog
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.InfoButton
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.library.ui.LibraryLabels
import com.abitusd.app.feature.train.ui.EditorInfo

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
    FormScreen(title = texts.name, subtitle = state.discipline, onBack = onBack, info = {
        InfoButton(R.string.info_screen_detail, EditorInfo.detail)
    }) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        ) {
            InfoPill(stringResource(R.string.difficulty_value, exercise.difficulty), emphasized = true)
            InfoPill(stringResource(LibraryLabels.metric(exercise.metricType)))
            InfoPill(stringResource(LibraryLabels.pattern(exercise.movementPattern)))
        }
        FavoriteButton(id)
        DetailParagraph(
            if (exercise.isOwn) R.string.detail_notes else R.string.detail_summary,
            if (exercise.isOwn) AppIcons.Notes else AppIcons.Info,
            texts.summary,
        )
        MusclesCard(state.primaryMuscles, state.secondaryMuscles, state.equipment)
        DetailParagraph(R.string.detail_setup, AppIcons.Setup, texts.setup)
        StepsCard(texts.execution)
        IconListCard(R.string.detail_cues, texts.cues, positive = true)
        IconListCard(R.string.detail_mistakes, texts.mistakes, positive = false)
        DetailParagraph(R.string.detail_safety, AppIcons.Safety, texts.safety)
        RelatedExercises(R.string.detail_regressions, AppIcons.Regression, state.regressions, onOpenExercise)
        RelatedExercises(R.string.detail_progressions, AppIcons.Progression, state.progressions, onOpenExercise)
        RelatedExercises(R.string.detail_alternatives, AppIcons.Alternative, state.alternatives, onOpenExercise)
        if (exercise.source != null && exercise.license != null) {
            Text(
                text = stringResource(R.string.detail_source, exercise.source, exercise.license),
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
        }
        if (exercise.isOwn) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                AppButton(stringResource(R.string.detail_edit), {
                    onEdit(id)
                }, Modifier.weight(1f), AppButtonStyle.Secondary, icon = AppIcons.Edit)
                AppButton(
                    stringResource(R.string.detail_delete),
                    { confirmDelete = true },
                    Modifier.weight(1f),
                    AppButtonStyle.Destructive,
                    icon = AppIcons.Delete,
                )
            }
        }
    }
    if (confirmDelete) {
        ConfirmDialog(
            title = stringResource(R.string.detail_delete_title),
            message = stringResource(R.string.detail_delete_text),
            confirmLabel = stringResource(R.string.detail_delete_confirm),
            dismissLabel = stringResource(R.string.detail_cancel),
            onConfirm = viewModel::delete,
            onDismiss = { confirmDelete = false },
        )
    }
}
