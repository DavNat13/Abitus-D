package com.abitusd.app.feature.train.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.ScheduledWorkoutEntity
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.train.ui.routines.Hint
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/** Workouts of the selected day with their actions, the day load and the week volume. */
@Composable
fun CalendarDayDetail(
    s: CalendarState,
    viewModel: CalendarViewModel,
) {
    var choosing by remember { mutableStateOf(false) }
    var moving by remember { mutableStateOf<ScheduledWorkoutEntity?>(null) }
    val rows = s.scheduled.filter { it.date == s.selected.toString() }
    Text(s.selected.toString(), style = AppTheme.typography.titleMedium, color = AppTheme.colors.textPrimary)
    if (rows.isEmpty()) Hint(stringResource(R.string.calendar_empty_day))
    rows.forEach { row ->
        Text(
            s.routineNames[row.routineId].orEmpty() + " - " + stringResource(statusLabel(row.status)),
            style = AppTheme.typography.body,
            color = AppTheme.colors.textPrimary,
        )
        row.rescheduledFrom?.let {
            Text(
                stringResource(R.string.calendar_rescheduled_from, it),
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xxs)) {
            if (row.status ==
                "planned"
            ) {
                TextButton(onClick = { viewModel.skip(row.id) }) { Text(stringResource(R.string.calendar_skip)) }
            }
            TextButton(
                onClick = { viewModel.moveTomorrow(row) },
            ) { Text(stringResource(R.string.calendar_move_tomorrow)) }
            TextButton(onClick = { moving = row }) { Text(stringResource(R.string.calendar_reschedule)) }
            TextButton(onClick = {
                viewModel.remove(row.id)
            }) { Text(stringResource(R.string.calendar_remove), color = AppTheme.colors.error) }
        }
    }
    if (s.dayLoad >
        0
    ) {
        Text(
            stringResource(R.string.calendar_day_load, s.dayLoad),
            style = AppTheme.typography.body,
            color = AppTheme.colors.textPrimary,
        )
    }
    AppButton(stringResource(R.string.calendar_add), { choosing = true }, style = AppButtonStyle.Secondary)
    if (s.weekVolume.isNotEmpty()) {
        Text(
            stringResource(R.string.calendar_week_volume),
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colors.textPrimary,
        )
        s.weekVolume.forEach { (muscle, sets) ->
            Text(
                "$muscle: ${"%.1f".format(sets)}",
                style = AppTheme.typography.body,
                color = AppTheme.colors.textPrimary,
            )
        }
    }
    if (choosing) {
        AlertDialog(
            onDismissRequest = { choosing = false },
            title = { Text(stringResource(R.string.calendar_choose_routine)) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    if (s.routineNames.isEmpty()) Hint(stringResource(R.string.train_no_routines))
                    s.routineNames.forEach { (id, name) ->
                        TextButton(onClick = {
                            choosing = false
                            viewModel.add(id)
                        }) { Text(name) }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { choosing = false },
                ) { Text(stringResource(R.string.detail_cancel)) }
            },
        )
    }
    moving?.let { row ->
        RescheduleDialog(LocalDate.parse(row.date), { moving = null }) { viewModel.reschedule(row.id, it) }
    }
}
