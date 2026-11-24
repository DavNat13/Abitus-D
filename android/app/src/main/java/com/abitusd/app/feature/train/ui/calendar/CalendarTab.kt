package com.abitusd.app.feature.train.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.train.domain.IcsBuilder
import com.abitusd.app.feature.train.domain.IcsEvent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarTab(viewModel: CalendarViewModel = hiltViewModel()) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]
    val context = LocalContext.current
    val shareTitle = stringResource(R.string.calendar_share_title)
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(
                rememberScrollState(),
            ).padding(horizontal = AppSpacing.screenMargin)
            .padding(bottom = AppSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        AppCard {
            Column(Modifier.padding(AppSpacing.sm), verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs)) {
                MonthHeader(
                    s.month.format(DateTimeFormatter.ofPattern("LLLL yyyy", locale)).replaceFirstChar {
                        it.titlecase(locale)
                    },
                    viewModel,
                )
                MonthGrid(s, locale, viewModel)
            }
        }
        CalendarDayDetail(s, viewModel)
        WeekAvailability(viewModel::fitWeek)
        AppButton(
            stringResource(R.string.calendar_export),
            {
                val events =
                    s.scheduled
                        .filter {
                            it.status != CalendarViewModel.STATUS_SKIPPED &&
                                LocalDate.parse(it.date).month == s.month.month
                        }.map {
                            IcsEvent(it.id, LocalDate.parse(it.date), s.routineNames[it.routineId].orEmpty())
                        }
                shareIcs(context, IcsBuilder.build(events), shareTitle)
            },
            style = AppButtonStyle.Tonal,
            icon = AppIcons.Share,
        )
    }
}

@Composable
private fun MonthHeader(
    title: String,
    viewModel: CalendarViewModel,
) {
    val colors = AppTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { viewModel.shiftMonth(-1) }) {
            Icon(
                painterResource(AppIcons.ChevronLeft),
                stringResource(R.string.calendar_previous),
                tint = colors.primary,
            )
        }
        Text(
            title,
            style = AppTheme.typography.titleMedium,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = { viewModel.shiftMonth(1) }) {
            Icon(painterResource(AppIcons.ChevronRight), stringResource(R.string.calendar_next), tint = colors.primary)
        }
    }
}

@Composable
private fun MonthGrid(
    s: CalendarState,
    locale: Locale,
    viewModel: CalendarViewModel,
) {
    Row {
        DayOfWeek.entries.forEach {
            Text(
                it.getDisplayName(TextStyle.NARROW, locale),
                style = AppTheme.typography.caption,
                textAlign = TextAlign.Center,
                color = AppTheme.colors.textSecondary,
                modifier = Modifier.weight(1f),
            )
        }
    }
    val first = s.month.atDay(1)
    val offset = first.dayOfWeek.value - 1
    val cells = (0 until WEEKS_SHOWN * DAYS).map { first.plusDays((it - offset).toLong()) }
    cells.chunked(DAYS).filter { week -> week.any { it.month == s.month.month } }.forEach { week ->
        Row {
            week.forEach { date ->
                DayCell(date, date.month == s.month.month, date == s.selected, statusOf(s, date), Modifier.weight(1f)) {
                    viewModel.select(date)
                }
            }
        }
    }
}

private const val DAYS = 7
private const val WEEKS_SHOWN = 6
