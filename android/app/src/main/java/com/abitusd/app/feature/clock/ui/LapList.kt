package com.abitusd.app.feature.clock.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Laps newest first with their split; the best and slowest split are labeled, not only colored. */
@Composable
fun LapList(marksMs: List<Long>) {
    val splits = marksMs.mapIndexed { i, ms -> ms - (marksMs.getOrNull(i - 1) ?: 0L) }
    val best = splits.minOrNull()
    val worst = splits.maxOrNull()
    splits.indices.reversed().forEach { i ->
        val split = splits[i]
        val tag =
            when {
                splits.size < 2 -> ""
                split == best -> " - " + stringResource(R.string.clock_lap_best)
                split == worst -> " - " + stringResource(R.string.clock_lap_worst)
                else -> ""
            }
        Text(
            stringResource(R.string.clock_lap_row, i + 1, ClockLabels.time(split, tenths = true)) + tag,
            style = AppTheme.typography.body,
            color = if (tag.isEmpty()) AppTheme.colors.textPrimary else AppTheme.colors.primary,
        )
    }
}
