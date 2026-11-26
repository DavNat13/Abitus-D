package com.abitusd.app.core.designsystem.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abitusd.app.core.designsystem.theme.AppTheme
import kotlin.math.abs

/**
 * Own line chart (Phase 6, no chart library): one series over time, the minimum and maximum as
 * text, and the exact value of the point closest to a tap. [describe] gives the screen reader text.
 */
@Composable
fun LineChart(
    points: List<Pair<Long, Double>>,
    format: (Double) -> String,
    describe: String,
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.primary,
) {
    if (points.isEmpty()) return
    val colors = AppTheme.colors
    var picked by remember(points) { mutableStateOf(points.lastIndex) }
    val minY = points.minOf { it.second }
    val maxY = points.maxOf { it.second }
    val minX = points.first().first
    val spanX = (points.last().first - minX).coerceAtLeast(1)
    val spanY = (maxY - minY).takeIf { it > 0 } ?: 1.0
    Column(modifier.fillMaxWidth()) {
        Text(format(points[picked].second), style = AppTheme.typography.titleMedium, color = colors.textPrimary)
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(HEIGHT.dp)
                .semantics { contentDescription = describe }
                .pointerInput(points) {
                    detectTapGestures { tap ->
                        picked =
                            points.indices.minBy { i ->
                                abs(xOf(points[i].first, minX, spanX, size.width.toFloat()) - tap.x)
                            }
                    }
                },
        ) {
            val pad = PAD.dp.toPx()

            fun at(i: Int) =
                Offset(
                    xOf(points[i].first, minX, spanX, size.width),
                    pad + (size.height - 2 * pad) * (1 - ((points[i].second - minY) / spanY).toFloat()),
                )
            drawLine(colors.outline, Offset(0f, size.height - pad), Offset(size.width, size.height - pad), 1.dp.toPx())
            val path = Path()
            points.indices.forEach { i -> if (i == 0) path.moveTo(at(i).x, at(i).y) else path.lineTo(at(i).x, at(i).y) }
            drawPath(path, color, style = Stroke(LINE.dp.toPx()))
            points.indices.forEach { i ->
                drawCircle(
                    color,
                    if (i ==
                        picked
                    ) {
                        DOT_BIG.dp.toPx()
                    } else {
                        DOT.dp.toPx()
                    },
                    at(i),
                )
            }
        }
        Text("${format(minY)} - ${format(maxY)}", style = AppTheme.typography.label, color = colors.textSecondary)
    }
}

private fun xOf(
    x: Long,
    minX: Long,
    spanX: Long,
    width: Float,
): Float {
    val pad = width * EDGE
    return pad + (width - 2 * pad) * ((x - minX).toFloat() / spanX)
}

private const val HEIGHT = 180
private const val PAD = 12
private const val LINE = 3
private const val DOT = 3
private const val DOT_BIG = 6
private const val EDGE = 0.04f
