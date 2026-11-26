@file:Suppress("MagicNumber") // Dial proportions.

package com.abitusd.app.feature.clock.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.abitusd.app.core.designsystem.theme.AppTheme
import kotlin.math.cos
import kotlin.math.sin

const val THEME_DIGITAL = "digital"
const val THEME_MINIMAL = "minimal"
const val THEME_ANALOG = "analog"

/** Clock styles (11e): progress ring, minimal (numbers only) or an analog dial with a hand. */
@Composable
fun ClockRing(
    theme: String,
    progress: Float,
    color: Color,
) {
    val colors = AppTheme.colors
    when (theme) {
        THEME_MINIMAL -> {
            Unit
        }

        THEME_ANALOG -> {
            Canvas(Modifier.size(RING.dp)) {
                val r = size.minDimension / 2
                drawCircle(colors.outline, r - STROKE, style = Stroke(STROKE / 2))
                for (i in 0 until TICKS) {
                    val a = Math.toRadians(i * DEG / TICKS - QUARTER)
                    val outer =
                        Offset(
                            center.x + (r - STROKE) * cos(a).toFloat(),
                            center.y + (r - STROKE) * sin(a).toFloat(),
                        )
                    val inner =
                        Offset(
                            center.x + (r - STROKE * 3) * cos(a).toFloat(),
                            center.y + (r - STROKE * 3) * sin(a).toFloat(),
                        )
                    drawLine(colors.textSecondary, inner, outer, STROKE / 3)
                }
                val a = Math.toRadians(progress * DEG - QUARTER)
                val tip =
                    Offset(
                        center.x + (r - STROKE * 4) * cos(a).toFloat(),
                        center.y + (r - STROKE * 4) * sin(a).toFloat(),
                    )
                drawLine(color, center, tip, STROKE / 1.5f)
                drawCircle(color, STROKE / 1.5f)
            }
        }

        else -> {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(RING.dp),
                color = color,
                strokeWidth = STROKE.dp,
                trackColor = colors.outline,
            )
        }
    }
}

private const val RING = 260
private const val STROKE = 10f
private const val TICKS = 12
private const val DEG = 360.0
private const val QUARTER = 90.0
