package com.abitusd.app.feature.progress.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.chart.LineChart
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.ConfirmDialog
import com.abitusd.app.core.designsystem.component.NumberField
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.progress.domain.BodyTrends
import com.abitusd.app.feature.progress.domain.ExerciseProgress
import com.abitusd.app.feature.progress.domain.Metric

/** Body measurements (11c): log, weight trend with goal, indices with their limits, relative strength. */
@Composable
fun MeasurementsTab(
    analyses: ProgressState,
    vm: MeasurementsViewModel = hiltViewModel(),
) {
    val s by vm.state.collectAsStateWithLifecycle()
    MeasurementForm(vm::save)
    WeightTrendCard(s, vm::setGoal)
    IndicesCard(s)
    RelativeStrengthCard(analyses, s.weights.lastOrNull()?.value)
    HistoryCard(s, vm::delete)
}

@Composable
private fun fmt(): (Double, Int) -> String {
    val locale = LocalConfiguration.current.locales[0]
    return { v, d -> String.format(locale, "%.${d}f", v) }
}

@Composable
private fun WeightTrendCard(
    s: MeasurementsState,
    onGoal: (Double?) -> Unit,
) {
    val f = fmt()
    val latest = s.weights.lastOrNull()?.value
    SectionCard(stringResource(R.string.measure_weight_trend), icon = AppIcons.Progress) {
        if (s.smoothed.size < 2) {
            Hint(R.string.measure_trend_empty)
        } else {
            LineChart(
                s.smoothed.map {
                    it.day * DAY_MS to it.value
                },
                { kg(it) },
                stringResource(R.string.measure_chart_desc, s.weights.size),
            )
        }
        val rate = s.rateKgWeek
        if (rate != null) Text(stringResource(R.string.measure_rate, f(rate, 2)), style = AppTheme.typography.body)
        if (rate != null && latest != null && BodyTrends.tooFast(rate, latest)) Hint(R.string.measure_too_fast)
        NumberField(s.goalKg, stringResource(R.string.measure_goal), { onGoal(it.toDoubleOrNull()) }, decimal = true)
        val goal = s.goalKg
        val weeks =
            if (latest != null && goal != null &&
                rate != null
            ) {
                BodyTrends.weeksToGoal(latest, goal, rate)
            } else {
                null
            }
        if (weeks !=
            null
        ) {
            Text(stringResource(R.string.measure_weeks_goal, f(weeks, 1)), style = AppTheme.typography.body)
        }
    }
}

@Composable
private fun IndicesCard(s: MeasurementsState) {
    val f = fmt()
    val latest = s.weights.lastOrNull()?.value
    val h = s.heightM
    SectionCard(stringResource(R.string.measure_indices), icon = AppIcons.Info) {
        if (h == null || latest == null) {
            Hint(R.string.measure_indices_missing)
        } else {
            BodyTrends.bmi(latest, h)?.let {
                Text(stringResource(R.string.measure_bmi, f(it, 1)), style = AppTheme.typography.body)
            }
            val waist = s.rows.lastOrNull { it.waistM != null }?.waistM
            waist
                ?.let {
                    BodyTrends.waistToHeight(it, h)
                }?.let { Text(stringResource(R.string.measure_whtr, f(it, 2)), style = AppTheme.typography.body) }
        }
        Hint(R.string.measure_indices_limits)
    }
}

@Composable
private fun RelativeStrengthCard(
    analyses: ProgressState,
    weight: Double?,
) {
    val f = fmt()
    val rows =
        analyses.analyses.filter {
            it.records.bestE1rm != null &&
                ExerciseProgress.metricOf(it.metricType) == Metric.E1RM
        }
    SectionCard(stringResource(R.string.measure_relative), icon = AppIcons.Weight) {
        if (weight == null || rows.isEmpty()) {
            Hint(R.string.measure_relative_empty)
        } else {
            rows.take(TOP).forEach { a ->
                BodyTrends.relativeStrength(a.records.bestE1rm ?: 0.0, weight)?.let {
                    Text(
                        stringResource(R.string.measure_relative_row, a.name, f(it, 2)),
                        style = AppTheme.typography.body,
                    )
                }
            }
        }
    }
}

private const val TOP = 8
private const val CM = 100.0
private const val DAY_MS = 86_400_000L
