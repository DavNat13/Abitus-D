package com.abitusd.app.feature.train.ui.programs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.component.ConfirmDialog
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.InfoButton
import com.abitusd.app.core.designsystem.component.ListItemCard
import com.abitusd.app.core.designsystem.component.NumberField
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.feature.train.ui.EditorInfo

@Composable
fun ProgramEditorScreen(
    programId: String?,
    onBack: () -> Unit,
    viewModel: ProgramEditorViewModel =
        hiltViewModel<ProgramEditorViewModel, ProgramEditorViewModel.Factory>(
            key = programId ?: "new",
            creationCallback = { it.create(programId) },
        ),
) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.done.collect { onBack() } }
    var editing by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    val names = s.routines.associate { it.id to it.name }
    FormScreen(title = stringResource(R.string.program_title), onBack = onBack, info = {
        InfoButton(R.string.info_screen_program, EditorInfo.program)
    }) {
        SectionCard(stringResource(R.string.routine_details), icon = AppIcons.Notes) {
            AppTextField(s.program.name, viewModel::setName, stringResource(R.string.program_name))
            NumberField(
                s.program.weeksCount,
                stringResource(R.string.program_weeks),
                { v -> v.toIntOrNull()?.let(viewModel::setWeeks) },
            )
        }
        if (s.days.any { it.activeRest }) StatusBanner(stringResource(R.string.program_active_rest), StatusKind.Success)
        repeat(s.program.weeksCount) { week -> WeekCard(week, s.days, names) { day -> editing = week to day } }
        s.error?.let { StatusBanner(stringResource(it), StatusKind.Error) }
        AppButton(stringResource(R.string.program_save), viewModel::save, icon = AppIcons.Check)
        AppButton(
            stringResource(R.string.program_activate),
            viewModel::activate,
            style = AppButtonStyle.Secondary,
            icon = AppIcons.Today,
        )
        if (!s.isNew) {
            AppButton(stringResource(R.string.routine_delete), {
                confirmDelete = true
            }, style = AppButtonStyle.Destructive, icon = AppIcons.Delete)
        }
    }
    editing?.let { (week, day) ->
        val pick: (DayChoice) -> Unit = {
            viewModel.setDay(week, day, it)
            editing = null
        }
        AppBottomSheet(
            stringResource(
                R.string.program_week,
                week + 1,
            ) + " - " + stringResource(R.string.program_day, day + 1),
            {
                editing =
                    null
            },
        ) {
            ListItemCard(stringResource(R.string.program_rest), { pick(DayChoice.Rest) }, icon = AppIcons.Rest)
            ListItemCard(stringResource(R.string.program_active_rest_day), {
                pick(DayChoice.ActiveRest)
            }, icon = AppIcons.ActiveRest)
            ListItemCard(
                stringResource(R.string.program_unassigned),
                { pick(DayChoice.Unassigned) },
                icon = AppIcons.Close,
            )
            s.routines.forEach { r -> ListItemCard(r.name, { pick(DayChoice.Train(r.id)) }, icon = AppIcons.Train) }
        }
    }
    if (confirmDelete) {
        ConfirmDialog(
            title = stringResource(R.string.program_delete_title),
            message = stringResource(R.string.program_delete_text),
            confirmLabel = stringResource(R.string.routine_delete),
            dismissLabel = stringResource(R.string.detail_cancel),
            onConfirm = viewModel::delete,
            onDismiss = { confirmDelete = false },
        )
    }
}
