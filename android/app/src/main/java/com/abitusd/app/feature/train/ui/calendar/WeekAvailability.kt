package com.abitusd.app.feature.train.ui.calendar

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppMultiDropdown
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import java.time.DayOfWeek
import java.time.format.TextStyle

/** "This week I can train on..." moves the week's planned workouts onto those days, in order. */
@Composable
fun WeekAvailability(onApply: (Set<Int>) -> Unit) {
    var open by rememberSaveable { mutableStateOf(false) }
    AppButton(
        stringResource(R.string.availability_open),
        { open = true },
        style = AppButtonStyle.Tonal,
        icon = AppIcons.Reschedule,
    )
    if (!open) return
    val locale = LocalConfiguration.current.locales[0]
    var days by rememberSaveable { mutableStateOf(setOf<Int>()) }
    val options =
        DayOfWeek.entries.map { day ->
            ChoiceOption(
                day.ordinal,
                day.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.titlecase(locale) },
            )
        }
    AppBottomSheet(stringResource(R.string.availability_title), { open = false }) {
        Text(
            stringResource(R.string.availability_text),
            style = AppTheme.typography.body,
            color = AppTheme.colors.textSecondary,
        )
        AppMultiDropdown(
            stringResource(R.string.availability_days),
            options,
            days,
            { day -> days = if (day in days) days - day else days + day },
        )
        AppButton(stringResource(R.string.availability_apply), {
            onApply(days)
            open = false
        }, enabled = days.isNotEmpty(), icon = AppIcons.Check)
    }
}
