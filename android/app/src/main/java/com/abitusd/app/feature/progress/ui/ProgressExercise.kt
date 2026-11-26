package com.abitusd.app.feature.progress.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.chart.LineChart
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.clock.ui.ClockLabels
import com.abitusd.app.feature.progress.data.ExerciseAnalysis
import com.abitusd.app.feature.progress.domain.ExerciseProgress
import com.abitusd.app.feature.progress.domain.Metric
import com.abitusd.app.feature.progress.domain.VerdictKind
import java.util.Locale

/** One exercise: its evolution, the verdict with the data behind it, records and best sets. */
@Composable
fun ProgressExercise(
    s: ProgressState,
    onSelect: (String) -> Unit,
) {
    val a = s.selected ?: return
    val options = s.trained.map { ChoiceOption(it.exerciseId, s.names[it.exerciseId].orEmpty()) }
    AppDropdown(stringResource(R.string.progress_exercise), options, a.exerciseId, { it?.let(onSelect) })
    val metric = ExerciseProgress.metricOf(a.metricType)
    val fmt = { v: Double -> formatValue(metric, v) }
    SectionCard(stringResource(R.string.progress_evolution), icon = AppIcons.Progress) {
        if (a.points.isEmpty()) {
            Hint(R.string.progress_no_points)
        } else {
            LineChart(
                a.points.map { it.at to it.value },
                fmt,
                stringResource(R.string.progress_chart_desc, a.name, a.points.size),
            )
        }
    }
    SectionCard(stringResource(verdictTitle(a.verdict.kind)), icon = AppIcons.Info) {
        Text(verdictLine(a), style = AppTheme.typography.body)
    }
    SectionCard(stringResource(R.string.progress_records), icon = AppIcons.Progression) {
        val r = a.records
        r.bestE1rm?.let {
            Text(
                stringResource(R.string.progress_record_e1rm, kg(it)),
                style = AppTheme.typography.body,
            )
        }
        r.maxLoadKg?.let {
            Text(
                stringResource(R.string.progress_record_load, kg(it)),
                style = AppTheme.typography.body,
            )
        }
        r.maxReps?.let { Text(stringResource(R.string.progress_record_reps, it), style = AppTheme.typography.body) }
        r.maxDurationS?.let {
            Text(
                stringResource(R.string.progress_record_time, ClockLabels.time(it * MS)),
                style = AppTheme.typography.body,
            )
        }
    }
    SectionCard(stringResource(R.string.progress_top), icon = AppIcons.History) {
        a.top.forEachIndexed { i, (set, score) ->
            val detail =
                listOfNotNull(
                    set.loadKg?.let {
                        kg(it)
                    },
                    set.addedKg?.let { "+" + kg(it) },
                    set.reps?.let { "x $it" },
                ).joinToString(" ")
            Text("${i + 1}. ${fmt(score)}  ($detail)", style = AppTheme.typography.body)
        }
    }
}

private const val MS = 1000L

internal fun kg(v: Double) = String.format(Locale.getDefault(), "%.1f kg", v)

internal fun formatValue(
    metric: Metric,
    v: Double,
): String =
    when (metric) {
        Metric.E1RM -> kg(v)
        Metric.DURATION -> ClockLabels.time((v * MS).toLong())
        Metric.REPS -> v.toInt().toString()
    }

internal fun verdictTitle(kind: VerdictKind) =
    when (kind) {
        VerdictKind.PROGRESS -> R.string.progress_verdict_up
        VerdictKind.STABLE -> R.string.progress_verdict_stable
        VerdictKind.REGRESS -> R.string.progress_verdict_down
        VerdictKind.NO_DATA -> R.string.progress_verdict_none
    }

/** The verifiable sentence behind the verdict. */
@Composable
internal fun verdictLine(a: ExerciseAnalysis): String {
    val v = a.verdict
    if (v.kind == VerdictKind.NO_DATA) return stringResource(R.string.progress_verdict_none_detail, v.points)
    val metric = ExerciseProgress.metricOf(a.metricType)
    val pct = String.format(androidx.compose.ui.platform.LocalConfiguration.current.locales[0], "%+.1f", v.changePct4w)
    return stringResource(
        R.string.progress_verdict_detail,
        formatValue(metric, v.first),
        formatValue(metric, v.last),
        v.spanDays,
        pct,
        v.points,
    )
}
