package com.abitusd.app.feature.train.ui.routines

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.component.ConfirmDialog
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.SectionHeader
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind
import com.abitusd.app.core.designsystem.component.VolumeBars
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.library.ui.list.LibraryScreen

@Composable
fun RoutineEditorScreen(
    routineId: String?,
    onBack: () -> Unit,
    viewModel: RoutineEditorViewModel =
        hiltViewModel<RoutineEditorViewModel, RoutineEditorViewModel.Factory>(
            key = routineId ?: "new",
            creationCallback = { it.create(routineId) },
        ),
) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(viewModel) { viewModel.done.collect { onBack() } }
    if (s.picking) {
        Box(Modifier.fillMaxSize().background(AppTheme.colors.background)) {
            LibraryScreen(onOpenExercise = viewModel::addExercise, onCreateExercise = {}, showCreate = false)
        }
        return
    }
    val title = if (s.isNew) R.string.routine_title_new else R.string.routine_title_edit
    FormScreen(title = stringResource(title), onBack = onBack) {
        SectionCard(stringResource(R.string.routine_details), icon = AppIcons.Notes) {
            AppTextField(s.routine.name, { v ->
                viewModel.editRoutine {
                    it.copy(name = v.take(MAX_NAME))
                }
            }, stringResource(R.string.routine_name))
            AppTextField(s.routine.folder, { v ->
                viewModel.editRoutine {
                    it.copy(folder = v.take(MAX_FOLDER))
                }
            }, stringResource(R.string.routine_folder))
            AppTextField(s.routine.notes, { v ->
                viewModel.editRoutine {
                    it.copy(notes = v.take(MAX_NOTES))
                }
            }, stringResource(R.string.routine_notes))
        }
        SectionHeader(stringResource(R.string.routine_exercises))
        s.items.forEachIndexed { index, item ->
            PlannedExerciseCard(
                item = item,
                name = s.names[item.exerciseId].orEmpty(),
                position = index + 1,
                isFirst = index == 0,
                isLast = index == s.items.lastIndex,
                onEdit = { transform -> viewModel.editItem(item.id, transform) },
                onMove = { delta -> viewModel.move(item.id, delta) },
                onRemove = { viewModel.remove(item.id) },
            )
        }
        AppButton(stringResource(R.string.routine_add_exercise), {
            viewModel.setPicking(true)
        }, style = AppButtonStyle.Tonal, icon = AppIcons.Add)
        if (s.volume.isNotEmpty()) {
            SectionCard(stringResource(R.string.routine_volume), icon = AppIcons.Muscles) {
                VolumeBars(s.volume)
            }
        }
        s.error?.let { StatusBanner(stringResource(it), StatusKind.Error) }
        AppButton(stringResource(R.string.routine_save), viewModel::save, icon = AppIcons.Check)
        if (!s.isNew) RoutineActions(s.routine.archived, viewModel) { confirmDelete = true }
    }
    if (confirmDelete) {
        ConfirmDialog(
            title = stringResource(R.string.routine_delete_title),
            message = stringResource(R.string.routine_delete_text),
            confirmLabel = stringResource(R.string.routine_delete),
            dismissLabel = stringResource(R.string.detail_cancel),
            onConfirm = { viewModel.run(RoutineAction.DELETE) },
            onDismiss = { confirmDelete = false },
        )
    }
}

private const val MAX_NAME = 80
private const val MAX_FOLDER = 60
private const val MAX_NOTES = 1000
