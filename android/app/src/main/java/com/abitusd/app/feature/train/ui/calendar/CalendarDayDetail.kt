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
import com.abitusd.app.core.designsystem.component.VolumeBars
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
    var timing by remember { mutableStateOf<Pair<ScheduledWorkoutEntity, String>?>(null) }
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
            val name =
                if (row.routineId ==
                    null
                ) {
                    stringResource(R.string.program_active_rest_day)
                } else {
                    s.routineNames[row.routineId].orEmpty()
                }
            WorkoutRow(row, name, viewModel, { moving = row }) { timing = row to name }
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
            VolumeBars(s.weekVolume)
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
    timing?.let { (row, name) ->
        TimeSheet(row.plannedTime, { timing = null }) { viewModel.setTime(row.id, it, name) }
    }
    moving?.let { row ->
        RescheduleDialog(LocalDate.parse(row.date), { moving = null }) { viewModel.reschedule(row.id, it) }
    }
}
