package com.abitusd.app.feature.train.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.ScheduledWorkoutEntity
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.IconBadge
import com.abitusd.app.core.designsystem.component.MenuAction
import com.abitusd.app.core.designsystem.component.OverflowMenu
import com.abitusd.app.core.designsystem.component.Stepper
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import java.time.LocalTime

/** One workout of the selected day: status, planned time and its actions. */
@Composable
internal fun WorkoutRow(
    row: ScheduledWorkoutEntity,
    name: String,
    viewModel: CalendarViewModel,
    onReschedule: () -> Unit,
    onTime: () -> Unit,
) {
    val actions =
        buildList {
            if (row.status == "planned") {
                add(MenuAction(stringResource(R.string.calendar_skip), AppIcons.Skip) { viewModel.skip(row.id) })
                add(MenuAction(stringResource(R.string.calendar_time), AppIcons.Timer, onClick = onTime))
            }
            add(
                MenuAction(
                    stringResource(R.string.calendar_move_tomorrow),
                    AppIcons.Today,
                ) {
                    viewModel.reschedule(
                        row.id,
                        java.time.LocalDate
                            .parse(row.date)
                            .plusDays(1),
                    )
                },
            )
            add(MenuAction(stringResource(R.string.calendar_reschedule), AppIcons.Reschedule, onClick = onReschedule))
            add(
                MenuAction(stringResource(R.string.calendar_remove), AppIcons.Delete, destructive = true) {
                    viewModel.remove(row.id)
                },
            )
        }
    val done = row.status == CalendarViewModel.STATUS_DONE
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        IconBadge(
            if (done) AppIcons.Check else AppIcons.Train,
            container = if (done) AppTheme.colors.successContainer else AppTheme.colors.primaryContainer,
            tint = if (done) AppTheme.colors.successText else AppTheme.colors.primary,
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.xxxs)) {
            Text(name, style = AppTheme.typography.titleMedium, color = AppTheme.colors.textPrimary)
            val status = stringResource(statusLabel(row.status))
            Text(
                row.plannedTime?.let { stringResource(R.string.calendar_status_time, status, it) } ?: status,
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
            row.rescheduledFrom?.let {
                Text(
                    stringResource(R.string.calendar_rescheduled_from, it),
                    style = AppTheme.typography.caption,
                    color = AppTheme.colors.textSecondary,
                )
            }
        }
        OverflowMenu(stringResource(R.string.common_more_actions), actions)
    }
}

/** Planned time in 15-minute steps; saving it sets a reminder at that time. */
@Composable
internal fun TimeSheet(
    current: String?,
    onDismiss: () -> Unit,
    onSave: (String?) -> Unit,
) {
    var minutes by remember {
        mutableIntStateOf(current?.let { LocalTime.parse(it).toSecondOfDay() / SECONDS } ?: DEFAULT)
    }
    val label = LocalTime.ofSecondOfDay(minutes * SECONDS.toLong()).toString()
    AppBottomSheet(stringResource(R.string.calendar_time), onDismiss) {
        Stepper(
            label = stringResource(R.string.calendar_time_label),
            value = label,
            minusDescription = stringResource(R.string.workout_decrease),
            plusDescription = stringResource(R.string.workout_increase),
            onMinus = { minutes = Math.floorMod(minutes - STEP, DAY) },
            onPlus = { minutes = Math.floorMod(minutes + STEP, DAY) },
        )
        Text(
            stringResource(R.string.calendar_time_hint),
            style = AppTheme.typography.caption,
            color = AppTheme.colors.textSecondary,
        )
        AppButton(stringResource(R.string.calendar_time_save), {
            onSave(label)
            onDismiss()
        }, icon = AppIcons.Check)
        if (current != null) {
            AppButton(stringResource(R.string.calendar_time_clear), {
                onSave(null)
                onDismiss()
            }, style = AppButtonStyle.Text, icon = AppIcons.Close)
        }
    }
}

private const val SECONDS = 60
private const val STEP = 15
private const val DAY = 24 * 60
private const val DEFAULT = 7 * 60
