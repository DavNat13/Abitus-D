package com.abitusd.app.feature.train.ui.programs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.NumberField
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.train.ui.routines.Hint

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
    val names = s.routines.associate { it.id to it.name }
    FormScreen(title = stringResource(R.string.program_title), onBack = onBack) {
        AppTextField(s.program.name, viewModel::setName, stringResource(R.string.program_name))
        NumberField(
            s.program.weeksCount,
            stringResource(R.string.program_weeks),
            { v -> v.toIntOrNull()?.let(viewModel::setWeeks) },
        )
        if (s.days.any { it.isRest }) Hint(stringResource(R.string.program_active_rest))
        repeat(s.program.weeksCount) { week ->
            Text(
                stringResource(R.string.program_week, week + 1),
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.textPrimary,
            )
            repeat(DAYS) { day ->
                val cell = s.days.firstOrNull { it.weekIndex == week && it.dayIndex == day }
                val label =
                    when {
                        cell == null -> stringResource(R.string.program_unassigned)
                        cell.isRest -> stringResource(R.string.program_rest)
                        else -> names[cell.routineId].orEmpty()
                    }
                TextButton(onClick = { editing = week to day }, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        stringResource(R.string.program_day, day + 1) + ": " + label,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
        s.error?.let { StatusBanner(stringResource(it), StatusKind.Error) }
        AppButton(stringResource(R.string.program_save), viewModel::save)
        AppButton(stringResource(R.string.program_activate), viewModel::activate, style = AppButtonStyle.Secondary)
        if (!s.isNew) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                AppButton(stringResource(R.string.routine_delete), viewModel::delete, style = AppButtonStyle.Text)
            }
        }
    }
    editing?.let { (week, day) ->
        val pick: (DayChoice) -> Unit = {
            viewModel.setDay(week, day, it)
            editing = null
        }
        AlertDialog(
            onDismissRequest = { editing = null },
            title = { Text(stringResource(R.string.program_day, day + 1)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    TextButton(onClick = { pick(DayChoice.Rest) }) { Text(stringResource(R.string.program_rest)) }
                    TextButton(
                        onClick = { pick(DayChoice.Unassigned) },
                    ) { Text(stringResource(R.string.program_unassigned)) }
                    s.routines.forEach { r -> TextButton(onClick = { pick(DayChoice.Train(r.id)) }) { Text(r.name) } }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { editing = null },
                ) { Text(stringResource(R.string.detail_cancel)) }
            },
        )
    }
}

private const val DAYS = 7
