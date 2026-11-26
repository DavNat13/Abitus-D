package com.abitusd.app.feature.progress.ui.body

import android.graphics.Region
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.abitusd.app.core.designsystem.theme.AppTheme

/** A single large body. Neutral regions are noninteractive; numeric alternatives live below. */
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
    val resources = LocalContext.current.resources
    val shapes = remember(front, resources) { BodyShapes.load(resources, front) }
    val onTapState = rememberUpdatedState(onTap)
    val regions =
        remember(shapes) {
            shapes.filter { it.key != null }.map { shape ->
                shape.key!! to
                    Region().apply {
                        setPath(
                            shape.path.asAndroidPath(),
                            Region(0, 0, BodyShapes.WIDTH.toInt(), BodyShapes.HEIGHT.toInt()),
                        )
                    }
            }
        }
    Canvas(
        modifier
            .aspectRatio(BodyShapes.WIDTH / BodyShapes.HEIGHT)
            .semantics { contentDescription = description }
            .pointerInput(regions) {
                detectTapGestures { tap ->
                    val scale = size.width / BodyShapes.WIDTH
                    regions
                        .firstOrNull { (_, region) ->
                            region.contains((tap.x / scale).toInt(), (tap.y / scale).toInt())
                        }?.let { onTapState.value(it.first) }
                }
            },
    ) {
        val factor = size.width / BodyShapes.WIDTH
        scale(factor, factor, pivot = Offset.Zero) {
            shapes.forEach { shape ->
                val fill = shape.key?.let(colorOf) ?: colors.surfaceSoft
                drawPath(shape.path, fill)
                drawPath(
                    shape.path,
                    if (shape.key != null && shape.key == selected) colors.textPrimary else colors.textSecondary,
                    style = Stroke((if (shape.key != null && shape.key == selected) SELECTED else OUTLINE) / factor),
                )
            }
        }
    }
}

private const val OUTLINE = 0.75f
private const val SELECTED = 3f
