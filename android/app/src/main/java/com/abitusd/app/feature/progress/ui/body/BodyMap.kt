package com.abitusd.app.feature.progress.ui.body

import android.graphics.Region
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.abitusd.app.core.designsystem.theme.AppTheme

/**
 * Front or back body map (09d, 2D stage). Each muscle is filled with [colorOf]; muscles without
 * data use a neutral tone. Tapping a muscle reports its svg key; values are shown next to it, so
 * color is never the only cue.
 */
@Composable
fun BodyMap(
    front: Boolean,
    colorOf: (String) -> Color?,
    selected: String?,
    description: String,
    onTap: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val shapes = if (front) BodyShapes.front else BodyShapes.back
    val paths = remember(front) { shapes.mapValues { BodyShapes.both(it.value) } }
    val outline = remember { BodyShapes.outline() }
    val details =
        remember(front) { (if (front) BodyShapes.frontDetail else BodyShapes.backDetail).map { BodyShapes.both(it) } }
    val regions =
        remember(front) {
            paths.mapValues { (_, p) ->
                Region().apply {
                    setPath(
                        p.asAndroidPath(),
                        Region(0, 0, BodyShapes.WIDTH.toInt(), BodyShapes.HEIGHT.toInt()),
                    )
                }
            }
        }
    Canvas(
        modifier
            .aspectRatio(BodyShapes.WIDTH / BodyShapes.HEIGHT)
            .semantics { contentDescription = description }
            .pointerInput(front) {
                detectTapGestures { tap ->
                    val k = size.width / BodyShapes.WIDTH
                    val x = (tap.x / k).toInt()
                    val y = (tap.y / k).toInt()
                    regions.entries.firstOrNull { it.value.contains(x, y) }?.let { onTap(it.key) }
                }
            },
    ) {
        val k = size.width / BodyShapes.WIDTH
        scale(k, k, pivot = Offset.Zero) {
            val line = Stroke(width = LINE / k)
            drawOval(colors.surface, Offset(HEAD_X, HEAD_Y), Size(HEAD_W, HEAD_H))
            drawOval(colors.textSecondary, Offset(HEAD_X, HEAD_Y), Size(HEAD_W, HEAD_H), style = line)
            drawPath(outline, colors.surface)
            paths.forEach { (key, p) -> drawPath(p, colorOf(key) ?: colors.outline) }
            details.forEach {
                drawPath(
                    it,
                    colors.textSecondary.copy(alpha = DETAIL_ALPHA),
                    style =
                        Stroke(
                            width =
                                DETAIL / k,
                        ),
                )
            }
            paths.forEach { (key, p) ->
                val chosen = key == selected
                drawPath(
                    p,
                    if (chosen) colors.textPrimary else colors.background,
                    style =
                        Stroke(
                            width =
                                (if (chosen) SELECTED else SEAM) / k,
                        ),
                )
            }
            drawPath(outline, colors.textSecondary, style = line)
        }
    }
}

private const val HEAD_X = 82f
private const val HEAD_Y = 6f
private const val HEAD_W = 36f
private const val HEAD_H = 44f
private const val LINE = 2.5f
private const val SEAM = 2f
private const val SELECTED = 4f
private const val DETAIL = 1.2f
private const val DETAIL_ALPHA = 0.5f
