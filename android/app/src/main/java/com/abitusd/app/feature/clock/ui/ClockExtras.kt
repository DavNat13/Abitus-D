package com.abitusd.app.feature.clock.ui

import android.app.DatePickerDialog
import android.content.Intent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.clock.data.ClockStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ClockExtrasViewModel
    @Inject
    constructor(
        val store: ClockStore,
        val records: com.abitusd.app.feature.clock.data.ClockRecords,
    ) : ViewModel()

/** Wall clock, countdown to a date, lap history with CSV export and the measured deviation (11e). */
@Composable
fun ClockExtras(vm: ClockExtrasViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    var now by remember { mutableStateOf(LocalTime.now()) }
    var target by remember { mutableStateOf(vm.store.targetDate?.let(LocalDate::parse)) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalTime.now()
            delay(SECOND_MS)
        }
    }
    SectionCard(stringResource(R.string.clock_wall), icon = AppIcons.Timer) {
        Text(
            now.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            style = AppTheme.typography.displayNumeric.copy(fontSize = WALL.sp),
        )
        vm.store.lastDriftMs.takeIf { it != Long.MIN_VALUE }?.let {
            Text(
                stringResource(R.string.clock_drift, it),
                style = AppTheme.typography.body,
                color = AppTheme.colors.textSecondary,
            )
        }
    }
    SectionCard(stringResource(R.string.clock_target), icon = AppIcons.Calendar) {
        target?.let { date ->
            val days =
                ChronoUnit.DAYS
                    .between(LocalDate.now(), date)
                    .coerceAtLeast(0)
                    .toInt()
            val label = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale))
            Text(stringResource(R.string.clock_target_days, days, label), style = AppTheme.typography.titleMedium)
        }
        AppButton(stringResource(R.string.clock_target_pick), {
            val start = target ?: LocalDate.now()
            DatePickerDialog(context, { _, y, m, d ->
                val picked = LocalDate.of(y, m + 1, d)
                vm.store.targetDate = picked.toString()
                target = picked
            }, start.year, start.monthValue - 1, start.dayOfMonth).show()
        }, style = AppButtonStyle.Tonal, icon = AppIcons.Calendar)
    }
    val history = remember { vm.records.history() }
    SectionCard(stringResource(R.string.clock_history), icon = AppIcons.Lap) {
        if (history.isEmpty()) {
            Text(
                stringResource(R.string.clock_history_empty),
                style = AppTheme.typography.body,
                color = AppTheme.colors.textSecondary,
            )
            return@SectionCard
        }
        history.take(SHOWN).forEach { record ->
            val splits = record.lapsMs.mapIndexed { i, ms -> ms - (record.lapsMs.getOrNull(i - 1) ?: 0L) }
            val t = { ms: Long -> ClockLabels.time(ms, tenths = true) }
            Text(
                stringResource(
                    R.string.clock_history_row,
                    record.name,
                    splits.size,
                    t(splits.min()),
                    t(splits.max()),
                    t(splits.sum() / splits.size),
                ),
                style = AppTheme.typography.body,
            )
        }
        AppButton(stringResource(R.string.clock_export_csv), {
            val csv =
                buildString {
                    appendLine("name,started_at_ms,lap,split_s,total_s")
                    history.forEach { r ->
                        r.lapsMs.forEachIndexed { i, ms ->
                            val split = ms - (r.lapsMs.getOrNull(i - 1) ?: 0L)
                            appendLine(
                                "\"${r.name.replace("\"", "\"\"")}\",${r.at},${i + 1},${split / MS_D},${ms / MS_D}",
                            )
                        }
                    }
                }
            val send = Intent(Intent.ACTION_SEND).setType("text/csv").putExtra(Intent.EXTRA_TEXT, csv)
            context.startActivity(Intent.createChooser(send, null))
        }, style = AppButtonStyle.Secondary, icon = AppIcons.Share)
    }
}

private const val SECOND_MS = 1_000L
private const val MS_D = 1000.0
private const val WALL = 48
private const val SHOWN = 5
