package com.abitusd.app.feature.clock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.clock.domain.ClockEngine
import com.abitusd.app.feature.clock.domain.ClockMode
import com.abitusd.app.feature.clock.domain.ClockRun
import com.abitusd.app.feature.clock.domain.ClockStatus

/** Large display readable at a distance; phase shown by color, label and progress ring. */
@Composable
fun ClockDisplay(
    run: ClockRun,
    status: ClockStatus,
    vm: ClockViewModel,
) {
    val colors = AppTheme.colors
    val countsUp = ClockMode.of(run.mode).countsUp
    val color = if (status.done) colors.textSecondary else ClockLabels.color(status.kind, colors)
    val label =
        when {
            status.done -> stringResource(R.string.clock_done)
            run.pausedAt != null -> stringResource(R.string.clock_paused)
            else -> stringResource(ClockLabels.kind(status.kind))
        }
    val shown = if (countsUp) status.elapsedMs else status.remainingMs
    val progress =
        if (status.phaseMs in
            1 until Long.MAX_VALUE
        ) {
            1f - status.remainingMs.toFloat() / status.phaseMs
        } else {
            0f
        }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(run.name, style = AppTheme.typography.titleMedium, color = colors.textSecondary)
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(RING.dp),
                color = color,
                strokeWidth = STROKE.dp,
                trackColor = colors.outline,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    label.uppercase(),
                    style = AppTheme.typography.titleLarge,
                    color = color,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
                Text(
                    ClockLabels.time(shown, tenths = countsUp && run.spec.phases.isEmpty()),
                    style = AppTheme.typography.displayNumeric.copy(fontSize = BIG.sp),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
                if (!countsUp) {
                    InfoPill(
                        stringResource(R.string.clock_round, status.round, status.rounds),
                        emphasized = true,
                    )
                }
            }
        }
    }
    ClockControls(run, status, countsUp, vm)
    if (run.lapsMs.isNotEmpty()) {
        SectionCard(stringResource(R.string.clock_laps), icon = AppIcons.Lap) {
            run.lapsMs.reversed().forEachIndexed { i, ms ->
                val n = run.lapsMs.size - i
                Text(
                    stringResource(R.string.clock_lap_row, n, ClockLabels.time(ms, tenths = true)),
                    style = AppTheme.typography.body,
                )
            }
        }
    }
}

private const val RING = 260
private const val STROKE = 10
private const val BIG = 64
