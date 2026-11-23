package com.abitusd.app.feature.workout.ui.active

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.theme.AppElevation
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.core.designsystem.theme.appShadow
import kotlinx.coroutines.delay

/** Persistent rest bar with time left, +15 s and skip. Hidden when no rest is running. */
@Composable
fun RestBar(
    endsAt: Long,
    totalS: Int,
    onAdd: () -> Unit,
    onSkip: () -> Unit,
) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(endsAt) {
        while (now < endsAt) {
            delay(TICK_MS)
            now = System.currentTimeMillis()
        }
    }
    val left = ((endsAt - now) / MS).coerceAtLeast(0)
    if (left <= 0) return
    val colors = AppTheme.colors
    Column(
        Modifier
            .fillMaxWidth()
            .padding(AppSpacing.sm)
            .appShadow(AppElevation.Lg, AppShapes.card)
            .background(colors.primaryContainer, AppShapes.card)
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(
                    R.string.workout_rest_left,
                    "%d:%02d".format(
                        left / SECONDS_PER_MIN,
                        left % SECONDS_PER_MIN,
                    ),
                ),
                style = AppTheme.typography.titleLarge,
                color = colors.onPrimaryContainer,
                modifier = Modifier.weight(1f).semantics { liveRegion = LiveRegionMode.Polite },
            )
            TextButton(
                onClick = onAdd,
            ) { Text(stringResource(R.string.rest_add_15), style = AppTheme.typography.label) }
            TextButton(onClick = onSkip) { Text(stringResource(R.string.rest_skip), style = AppTheme.typography.label) }
        }
        LinearProgressIndicator(
            progress = { if (totalS > 0) (left.toFloat() / totalS).coerceIn(0f, 1f) else 0f },
            modifier = Modifier.fillMaxWidth(),
            color = colors.primary,
            trackColor = colors.surface,
        )
    }
}

private const val TICK_MS = 250L
private const val MS = 1000L
private const val SECONDS_PER_MIN = 60
