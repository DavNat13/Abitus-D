package com.abitusd.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
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
 */
@Composable
fun VolumeBars(
    rows: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
) {
    val max = rows.maxOfOrNull { it.second }?.takeIf { it > 0.0 } ?: return
    val colors = AppTheme.colors
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        rows.forEach { (muscle, sets) ->
            val rounded = (sets * HALF_STEPS).roundToInt() / HALF_STEPS
            val count = pluralStringResource(R.plurals.volume_sets, rounded.roundToInt(), formatSets(rounded))
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
