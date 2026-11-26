package com.abitusd.app.feature.progress.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.progress.RangeTotals
import com.abitusd.app.core.designsystem.component.NavRow
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.progress.data.ExerciseAnalysis
import com.abitusd.app.feature.progress.domain.VerdictKind
import kotlin.math.roundToLong

/** This week against last week, exercises that progress, and exercises without progress. */
@Composable
fun ProgressSummary(
    s: ProgressState,
    isStagnant: (ExerciseAnalysis) -> Boolean,
    onOpen: (String) -> Unit,
) {
    SectionCard(stringResource(R.string.progress_week), icon = AppIcons.Today) {
        WeekLine(R.string.progress_sessions, s.thisWeek?.sessions?.toLong(), s.lastWeek?.sessions?.toLong())
        WeekLine(R.string.progress_sets, s.thisWeek?.sets?.toLong(), s.lastWeek?.sets?.toLong())
        WeekLine(R.string.progress_volume, s.thisWeek?.volumeKg?.roundToLong(), s.lastWeek?.volumeKg?.roundToLong())
        WeekLine(R.string.progress_minutes, s.thisWeek?.minutes, s.lastWeek?.minutes)
    }
    val up = s.analyses.filter { it.verdict.kind == VerdictKind.PROGRESS }
    SectionCard(stringResource(R.string.progress_improving), icon = AppIcons.Progression) {
        if (up.isEmpty()) Hint(R.string.progress_improving_none)
        up.forEach { NavRow("${it.name}: ${verdictLine(it)}", { onOpen(it.exerciseId) }) }
    }
    val stuck = s.analyses.filter(isStagnant)
    SectionCard(stringResource(R.string.progress_stagnant), icon = AppIcons.Regression) {
        if (stuck.isEmpty()) Hint(R.string.progress_stagnant_none)
        stuck.forEach {
            NavRow(
                stringResource(R.string.progress_stagnant_row, it.name, it.weeksSinceRecord ?: 0),
                { onOpen(it.exerciseId) },
            )
        }
    }
}

@Composable
private fun WeekLine(
    label: Int,
    now: Long?,
    before: Long?,
) {
    Text(
        stringResource(R.string.progress_week_row, stringResource(label), now ?: 0, before ?: 0),
        style = AppTheme.typography.body,
    )
}

@Composable
internal fun Hint(text: Int) {
    Text(stringResource(text), style = AppTheme.typography.body, color = AppTheme.colors.textSecondary)
}
