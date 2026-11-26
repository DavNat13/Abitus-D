package com.abitusd.app.feature.progress.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.progress.RangeTotals
import com.abitusd.app.core.designsystem.component.IconRow
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import kotlin.math.abs
import kotlin.math.roundToLong

@Composable
internal fun ProgressMetrics(
    now: RangeTotals?,
    before: RangeTotals?,
) {
    val metrics =
        listOf(
            MetricTile(
                R.string.progress_sessions,
                AppIcons.Calendar,
                now?.sessions?.toLong(),
                before?.sessions?.toLong(),
            ),
            MetricTile(R.string.progress_sets, AppIcons.Train, now?.sets?.toLong(), before?.sets?.toLong()),
            MetricTile(
                R.string.progress_volume,
                AppIcons.Weight,
                now?.volumeKg?.roundToLong(),
                before?.volumeKg?.roundToLong(),
            ),
            MetricTile(R.string.progress_minutes, AppIcons.Timer, now?.minutes, before?.minutes),
        )
    if (LocalDensity.current.fontScale > 1f) {
        metrics.forEach { MetricCard(it) }
    } else {
        metrics.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                pair.forEach { metric -> Column(Modifier.weight(1f)) { MetricCard(metric) } }
            }
        }
    }
}

private data class MetricTile(
    val label: Int,
    val icon: Int,
    val now: Long?,
    val before: Long?,
)

@Composable
private fun MetricCard(metric: MetricTile) {
    val delta = (metric.now ?: 0) - (metric.before ?: 0)
    val label =
        when {
            delta > 0 -> R.string.progress_change_more
            delta < 0 -> R.string.progress_change_less
            else -> R.string.progress_change_same
        }
    val icon =
        when {
            delta > 0 -> AppIcons.MoveUp
            delta < 0 -> AppIcons.MoveDown
            else -> AppIcons.Alternative
        }
    SectionCard(stringResource(metric.label), icon = metric.icon) {
        Text((metric.now ?: 0).toString(), style = AppTheme.typography.headline)
        IconRow(icon, stringResource(label, abs(delta)), tint = AppTheme.colors.textSecondary)
    }
}
