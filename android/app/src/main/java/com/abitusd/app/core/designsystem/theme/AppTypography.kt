package com.abitusd.app.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.abitusd.app.R

private const val REGULAR = 400
private const val MEDIUM = 500
private const val BOLD = 700

private fun roboto(weight: Int) =
    Font(
        resId = R.font.roboto,
        weight = FontWeight(weight),
        variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
    )

/** Roboto shipped with the app (variable font, OFL 1.1), weights 400, 500 and 700. */
val Roboto = FontFamily(roboto(REGULAR), roboto(MEDIUM), roboto(BOLD))

/** Type scale (05b-UI-TOKENS.md). */
@Immutable
data class AppTypography(
    val displayNumeric: TextStyle,
    val headline: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val body: TextStyle,
    val label: TextStyle,
    val caption: TextStyle,
)

private fun style(
    size: Int,
    lineHeight: Int,
    weight: FontWeight,
) = TextStyle(fontFamily = Roboto, fontSize = size.sp, lineHeight = lineHeight.sp, fontWeight = weight)

val DefaultAppTypography =
    AppTypography(
        displayNumeric = style(48, 56, FontWeight.Bold).copy(fontFeatureSettings = "tnum"),
        headline = style(28, 34, FontWeight.Bold),
        titleLarge = style(22, 28, FontWeight.Medium),
        titleMedium = style(18, 24, FontWeight.Medium),
        body = style(16, 24, FontWeight.Normal),
        label = style(14, 20, FontWeight.Medium),
        caption = style(12, 16, FontWeight.Normal),
    )
