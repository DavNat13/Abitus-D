@file:Suppress("MagicNumber") // Drawing proportions and the IWF plate color table.

package com.abitusd.app.feature.workout.ui.active

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abitusd.app.core.designsystem.theme.AppTheme

/**
 * Loaded barbell drawn to scale (11f): plates per side, largest inside, with the competition color
 * code (IWF: 25 red, 20 blue, 15 yellow, 10 green, 5 white) and every plate labeled with its weight,
 * so color is never the only cue.
 */
@Composable
fun PlateBar(
    perSide: List<Double>,
    description: String,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val measurer = rememberTextMeasurer()
    Canvas(modifier.fillMaxWidth().height(HEIGHT.dp).semantics { contentDescription = description }) {
        val midY = size.height / 2
        val shaft = size.height * SHAFT
        drawRect(colors.textSecondary, Offset(0f, midY - shaft / 2), Size(size.width, shaft))
        val unit = size.width / UNITS
        val plates = perSide.sortedDescending()
        var x = size.width / 2 + unit * COLLAR
        drawRect(
            colors.textPrimary,
            Offset(size.width / 2 - unit * COLLAR, midY - shaft * 2),
            Size(
                unit * COLLAR * 2,
                shaft * 4,
            ),
        )
        for (kg in plates) {
            val (fill, ink) = plateColors(kg, colors.outline)
            val h = size.height * heightFactor(kg)
            val w = unit * widthFactor(kg)
            for (left in listOf(x, size.width - x - w)) {
                drawRoundRect(fill, Offset(left, midY - h / 2), Size(w, h), CornerRadius(w / 4))
                drawRoundRect(
                    colors.textPrimary,
                    Offset(left, midY - h / 2),
                    Size(w, h),
                    CornerRadius(w / 4),
                    style =
                        androidx.compose.ui.graphics.drawscope
                            .Stroke(1f),
                )
            }
            val label = measurer.measure(formatPlate(kg), TextStyle(fontSize = LABEL.sp, color = ink))
            if (label.size.width <= w) {
                drawText(label, topLeft = Offset(x + (w - label.size.width) / 2, midY - label.size.height / 2))
            }
            x += w + unit * GAP
        }
    }
}

private fun formatPlate(kg: Double) = if (kg % 1.0 == 0.0) kg.toInt().toString() else kg.toString()

private fun heightFactor(kg: Double) =
    when {
        kg >= BIG_KG -> 1f
        kg >= MID_KG -> MID_H
        kg >= SMALL_KG -> SMALL_H
        else -> TINY_H
    }

private fun widthFactor(kg: Double) = if (kg >= MID_KG) WIDE else NARROW

private fun plateColors(
    kg: Double,
    other: Color,
): Pair<Color, Color> =
    when (kg) {
        25.0 -> Color(0xFFC62828) to Color.White
        20.0 -> Color(0xFF1565C0) to Color.White
        15.0 -> Color(0xFFF9A825) to Color.Black
        10.0 -> Color(0xFF2E7D32) to Color.White
        5.0 -> Color.White to Color.Black
        2.5 -> Color(0xFFC62828) to Color.White
        else -> other to Color.Black
    }

private const val HEIGHT = 120
private const val SHAFT = 0.08f
private const val UNITS = 40f
private const val COLLAR = 1.2f
private const val GAP = 0.15f
private const val WIDE = 1.8f
private const val NARROW = 1.1f
private const val LABEL = 10
private const val BIG_KG = 10.0
private const val MID_KG = 5.0
private const val SMALL_KG = 2.5
private const val MID_H = 0.55f
private const val SMALL_H = 0.42f
private const val TINY_H = 0.34f
