package com.abitusd.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import kotlin.math.roundToInt

/**
 * Planned sets per muscle as horizontal bars (05d): name, a bar relative to the largest value
 * and the number of sets. Numbers are always written, never only colors (05: accessibility).
 * An optional weekly target per row is drawn as a soft band behind the bar and written as text.
 */
@Composable
fun VolumeBars(
    rows: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    targets: List<IntRange?> = emptyList(),
) {
    val top = maxOf(rows.maxOfOrNull { it.second } ?: 0.0, targets.maxOfOrNull { it?.last ?: 0 }?.toDouble() ?: 0.0)
    val max = top.takeIf { it > 0.0 } ?: return
    val colors = AppTheme.colors
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        rows.forEachIndexed { index, (muscle, sets) ->
            val rounded = (sets * HALF_STEPS).roundToInt() / HALF_STEPS
            val target = targets.getOrNull(index)
            val plain = pluralStringResource(R.plurals.volume_sets, rounded.roundToInt(), formatSets(rounded))
            val count =
                target?.let { stringResource(R.string.volume_target, formatSets(rounded), it.first, it.last) } ?: plain
            Column(
                Modifier.semantics(mergeDescendants = true) {
                    contentDescription = "$muscle, $count"
                },
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs),
            ) {
                Row {
                    Text(
                        muscle,
                        style = AppTheme.typography.body,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    Text(count, style = AppTheme.typography.label, color = colors.textSecondary)
                }
                Box(Modifier.fillMaxWidth().height(AppSizes.volumeBar).background(colors.surfaceSoft, AppShapes.pill)) {
                    target?.let { TargetBand(it, max) }
                    Box(
                        Modifier
                            .fillMaxWidth(
                                (sets / max).toFloat().coerceIn(MIN_FRACTION, 1f),
                            ).height(AppSizes.volumeBar)
                            .background(colors.primary, AppShapes.pill),
                    )
                }
            }
        }
    }
}

@Composable
private fun TargetBand(
    target: IntRange,
    max: Double,
) {
    Row(Modifier.fillMaxWidth()) {
        val start = (target.first / max).toFloat().coerceIn(0f, 1f)
        val width = ((target.last - target.first) / max).toFloat().coerceIn(MIN_FRACTION, 1f - start)
        if (start > 0f) Spacer(Modifier.weight(start))
        Box(
            Modifier
                .weight(
                    width,
                ).height(AppSizes.volumeBar)
                .background(AppTheme.colors.success.copy(alpha = BAND_ALPHA), AppShapes.pill),
        )
        if (1f - start - width > 0f) Spacer(Modifier.weight(1f - start - width))
    }
}

private fun formatSets(value: Double): String =
    if (value % 1.0 ==
        0.0
    ) {
        value.toLong().toString()
    } else {
        "%.1f".format(value)
    }

private const val HALF_STEPS = 2.0
private const val MIN_FRACTION = 0.04f
private const val BAND_ALPHA = 0.45f
