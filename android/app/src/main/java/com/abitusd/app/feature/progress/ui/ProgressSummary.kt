package com.abitusd.app.feature.progress.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.component.ListItemCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.progress.data.ExerciseAnalysis
import com.abitusd.app.feature.progress.domain.VerdictKind

/** Weekly figures and actionable exercise cards, with the evidence behind every verdict. */
@Composable
fun ProgressSummary(
    s: ProgressState,
    isStagnant: (ExerciseAnalysis) -> Boolean,
    onOpen: (String) -> Unit,
) {
    Text(stringResource(R.string.progress_week), style = AppTheme.typography.titleMedium)
    ProgressMetrics(s.thisWeek, s.lastWeek)
    val up = s.analyses.filter { it.verdict.kind == VerdictKind.PROGRESS }
    Text(stringResource(R.string.progress_improving), style = AppTheme.typography.titleMedium)
    if (up.isEmpty()) Hint(R.string.progress_improving_none)
    up.forEach { AnalysisCard(it, AppIcons.Progression, onOpen) }
    val stuck = s.analyses.filter(isStagnant)
    Text(stringResource(R.string.progress_stagnant), style = AppTheme.typography.titleMedium)
    if (stuck.isEmpty()) Hint(R.string.progress_stagnant_none)
    stuck.forEach { AnalysisCard(it, AppIcons.Regression, onOpen) }
}

@Composable
private fun AnalysisCard(
    a: ExerciseAnalysis,
    icon: Int,
    onOpen: (String) -> Unit,
) {
    ListItemCard(a.name, { onOpen(a.exerciseId) }, icon = icon)
    InfoPill(stringResource(verdictTitle(a.verdict.kind)), emphasized = a.verdict.kind == VerdictKind.PROGRESS)
    Text(verdictLine(a), style = AppTheme.typography.caption, color = AppTheme.colors.textSecondary)
}

@Composable
internal fun Hint(text: Int) {
    Text(stringResource(text), style = AppTheme.typography.body, color = AppTheme.colors.textSecondary)
}
