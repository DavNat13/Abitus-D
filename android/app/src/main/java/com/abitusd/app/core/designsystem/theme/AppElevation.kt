package com.abitusd.app.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Shadow levels of 05c, tinted with ink. Dark theme halves them and relies on borders. */
@Suppress("MagicNumber") // Token values of 05c.
enum class AppElevation(
    val elevation: Dp,
    val alpha: Float,
) {
    None(0.dp, 0f),
    Sm(2.dp, 0.10f),
    Md(6.dp, 0.12f),
    Lg(14.dp, 0.16f),
    Nav(8.dp, 0.10f),
}

/** Draws a tinted shadow of [level] behind the element. */
@Composable
fun Modifier.appShadow(
    level: AppElevation,
    shape: Shape,
): Modifier {
    if (level == AppElevation.None) return this
    val colors = AppTheme.colors
    val tint = colors.ink.copy(alpha = level.alpha)
    val elevation = if (colors.isDark) level.elevation / 2 else level.elevation
    return shadow(elevation, shape, clip = false, ambientColor = tint, spotColor = tint)
}
