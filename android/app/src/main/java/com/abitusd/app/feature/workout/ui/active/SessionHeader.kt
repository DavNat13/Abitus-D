package com.abitusd.app.feature.workout.ui.active

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import kotlinx.coroutines.delay

/** Session header: back, elapsed time, undo, redo, exercise map and Finish. */
@Composable
fun SessionHeader(
    startedAt: Long?,
    canUndo: Boolean,
    canRedo: Boolean,
    onBack: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onMap: () -> Unit,
    onFinish: () -> Unit,
) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(TICK_MS)
            now = System.currentTimeMillis()
        }
    }
    val colors = AppTheme.colors
    Row(
        Modifier.fillMaxWidth().padding(horizontal = AppSpacing.xs, vertical = AppSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(painterResource(AppIcons.Back), stringResource(R.string.common_back), tint = colors.textPrimary)
        }
        Column(Modifier.weight(1f)) {
            Text(
                stringResource(R.string.workout_elapsed),
                style = AppTheme.typography.caption,
                color = colors.textSecondary,
            )
            Text(elapsed(startedAt, now), style = AppTheme.typography.titleLarge, color = colors.textPrimary)
        }
        IconButton(onClick = onUndo, enabled = canUndo) {
            Icon(painterResource(AppIcons.Undo), stringResource(R.string.workout_undo))
        }
        IconButton(onClick = onRedo, enabled = canRedo) {
            Icon(painterResource(AppIcons.Redo), stringResource(R.string.workout_redo))
        }
        IconButton(onClick = onMap) {
            Icon(painterResource(AppIcons.Steps), stringResource(R.string.workout_map), tint = colors.primary)
        }
        Button(
            onClick = onFinish,
            modifier = Modifier.padding(end = AppSpacing.xs),
            shape = AppShapes.button,
            colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary),
        ) { Text(stringResource(R.string.workout_finish), style = AppTheme.typography.label, maxLines = 1) }
    }
}

private fun elapsed(
    startedAt: Long?,
    now: Long,
): String {
    val total = ((now - (startedAt ?: now)) / MS).coerceAtLeast(0)
    val h = total / SECONDS_PER_HOUR
    val m = (total % SECONDS_PER_HOUR) / SECONDS_PER_MIN
    val s = total % SECONDS_PER_MIN
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}

private const val TICK_MS = 1000L
private const val MS = 1000L
private const val SECONDS_PER_MIN = 60
private const val SECONDS_PER_HOUR = 3600
