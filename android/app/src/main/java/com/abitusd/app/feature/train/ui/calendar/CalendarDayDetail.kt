package com.abitusd.app.feature.train.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.ScheduledWorkoutEntity
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.IconBadge
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.component.ListItemCard
import com.abitusd.app.core.designsystem.component.MenuAction
import com.abitusd.app.core.designsystem.component.OverflowMenu
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.train.ui.routines.Hint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Selected day card (workouts and their actions), then the planned volume of its week. */
@Composable
fun CalendarDayDetail(
    s: CalendarState,
    viewModel: CalendarViewModel,
) {
    var choosing by remember { mutableStateOf(false) }
    var moving by remember { mutableStateOf<ScheduledWorkoutEntity?>(null) }
    val locale = LocalConfiguration.current.locales[0]
    val rows = s.scheduled.filter { it.date == s.selected.toString() }
    val title =
        s.selected.format(DateTimeFormatter.ofPattern("EEEE d MMMM", locale)).replaceFirstChar {
            it.titlecase(locale)
        }
    SectionCard(title, icon = AppIcons.Today) {
        if (rows.isEmpty()) Hint(stringResource(R.string.calendar_empty_day))
        rows.forEachIndexed { index, row ->
            if (index > 0) HorizontalDivider(color = AppTheme.colors.outline)
            WorkoutRow(row, s.routineNames[row.routineId].orEmpty(), viewModel) { moving = row }
        }
        if (s.dayLoad > 0) InfoPill(stringResource(R.string.calendar_day_load, s.dayLoad), emphasized = true)
        AppButton(
            stringResource(R.string.calendar_add),
            { choosing = true },
            style = AppButtonStyle.Tonal,
            icon = AppIcons.Add,
        )
    }
    if (s.weekVolume.isNotEmpty()) {
        SectionCard(stringResource(R.string.calendar_week_volume), icon = AppIcons.Muscles) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                s.weekVolume.forEach { (muscle, sets) -> InfoPill("$muscle  ${"%.1f".format(sets)}") }
            }
        }
    }
    if (choosing) {
        AppBottomSheet(stringResource(R.string.calendar_choose_routine), { choosing = false }) {
            if (s.routineNames.isEmpty()) Hint(stringResource(R.string.train_no_routines))
            s.routineNames.forEach { (id, name) ->
                ListItemCard(name, {
                    choosing = false
                    viewModel.add(id)
                }, icon = AppIcons.Train)
            }
        }
    }
    moving?.let { row ->
        RescheduleDialog(LocalDate.parse(row.date), { moving = null }) { viewModel.reschedule(row.id, it) }
    }
}

@Composable
private fun WorkoutRow(
    row: ScheduledWorkoutEntity,
    name: String,
    viewModel: CalendarViewModel,
    onReschedule: () -> Unit,
) {
    val skip = stringResource(R.string.calendar_skip)
    val tomorrow = stringResource(R.string.calendar_move_tomorrow)
    val reschedule = stringResource(R.string.calendar_reschedule)
    val remove = stringResource(R.string.calendar_remove)
    val actions =
        buildList {
            if (row.status == "planned") add(MenuAction(skip, AppIcons.Skip) { viewModel.skip(row.id) })
            add(MenuAction(tomorrow, AppIcons.Today) { viewModel.moveTomorrow(row) })
            add(MenuAction(reschedule, AppIcons.Reschedule, onClick = onReschedule))
            add(MenuAction(remove, AppIcons.Delete, destructive = true) { viewModel.remove(row.id) })
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
            Text(
                stringResource(statusLabel(row.status)),
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
