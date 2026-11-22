package com.abitusd.app.feature.train.ui.routines

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind
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
    LaunchedEffect(viewModel) { viewModel.done.collect { onBack() } }
    if (s.picking) {
        Box(Modifier.fillMaxSize().background(AppTheme.colors.background)) {
            LibraryScreen(onOpenExercise = viewModel::addExercise, onCreateExercise = {}, showCreate = false)
        }
        return
    }
    val copySuffix = stringResource(R.string.routine_copy_suffix)
    val title = if (s.isNew) R.string.routine_title_new else R.string.routine_title_edit
    FormScreen(title = stringResource(title), onBack = onBack) {
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
        s.items.forEachIndexed { index, item ->
            PlannedExerciseCard(
                item = item,
                name = s.names[item.exerciseId].orEmpty(),
                isFirst = index == 0,
                isLast = index == s.items.lastIndex,
                onEdit = { transform -> viewModel.editItem(item.id, transform) },
                onMove = { delta -> viewModel.move(item.id, delta) },
                onRemove = { viewModel.remove(item.id) },
            )
        }
        AppButton(stringResource(R.string.routine_add_exercise), {
            viewModel.setPicking(true)
        }, style = AppButtonStyle.Secondary)
        if (s.volume.isNotEmpty()) {
            Text(
                stringResource(R.string.routine_volume),
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.textPrimary,
            )
            s.volume.forEach { (muscle, sets) ->
                Text(
                    "$muscle: ${"%.1f".format(sets)}",
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.textPrimary,
                )
            }
        }
        s.error?.let { StatusBanner(stringResource(it), StatusKind.Error) }
        AppButton(stringResource(R.string.routine_save), viewModel::save)
        if (!s.isNew) {
            AppButton(
                stringResource(R.string.routine_schedule),
                { viewModel.run(RoutineAction.SCHEDULE_TODAY) },
                style = AppButtonStyle.Secondary,
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                AppButton(stringResource(R.string.routine_duplicate), {
                    viewModel.run(RoutineAction.DUPLICATE, copySuffix)
                }, Modifier.weight(1f), AppButtonStyle.Text)
                val archive = if (s.routine.archived) R.string.routine_unarchive else R.string.routine_archive
                AppButton(
                    stringResource(archive),
                    { viewModel.run(RoutineAction.ARCHIVE) },
                    Modifier.weight(1f),
                    AppButtonStyle.Text,
                )
                AppButton(
                    stringResource(R.string.routine_delete),
                    { viewModel.run(RoutineAction.DELETE) },
                    Modifier.weight(1f),
                    AppButtonStyle.Text,
                )
            }
        }
    }
}

private const val MAX_NAME = 80
private const val MAX_FOLDER = 60
private const val MAX_NOTES = 1000
