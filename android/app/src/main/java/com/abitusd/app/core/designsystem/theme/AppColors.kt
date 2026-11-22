package com.abitusd.app.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Semantic color tokens (05b-UI-TOKENS.md). Screens never use raw colors. */
@Immutable
data class AppColors(
    val background: Color,
    val surface: Color,
    val surfaceSoft: Color,
    val outline: Color,
    val outlineStrong: Color,
    val inputBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDisabled: Color,
    val primary: Color,
    val primaryPressed: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val success: Color,
    val successText: Color,
    val successContainer: Color,
    val error: Color,
    val errorContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val ink: Color,
    val navy: Color,
    val isDark: Boolean,
)

private val Ink = Color(0xFF101632)
private val Navy = Color(0xFF001667)
private val Primary = Color(0xFF0135A8)
private val Green = Color(0xFF547F43)
private val Lavender = Color(0xFFF7F4FF)
private val White = Color(0xFFFFFFFF)

val LightAppColors =
    AppColors(
        background = White,
        surface = White,
        surfaceSoft = Lavender,
        outline = Color(0xFFDEDEE2),
        outlineStrong = Color(0xFFBCBEC6),
        inputBorder = Color(0xFF888B99),
        textPrimary = Ink,
        textSecondary = Color(0xFF5C6174),
        textDisabled = Color(0xFF9FA2AD),
        primary = Primary,
        primaryPressed = Navy,
        onPrimary = White,
        primaryContainer = Color(0xFFE6EBF6),
        onPrimaryContainer = Navy,
        success = Green,
        successText = Color(0xFF456B35),
        successContainer = Color(0xFFEAF0E8),
        error = Color(0xFFB3261E),
        errorContainer = Color(0xFFFBEAE9),
        warning = Color(0xFFA15C00),
        warningContainer = Color(0xFFFBF1E1),
        ink = Ink,
        navy = Navy,
        isDark = false,
    )

val DarkAppColors =
    AppColors(
        background = Ink,
        surface = Color(0xFF1E243E),
        surfaceSoft = Color(0xFF282D46),
        outline = Color(0xFF363B53),
        outlineStrong = Color(0xFF4A4F66),
        inputBorder = Color(0xFF777A8C),
        textPrimary = Lavender,
        textSecondary = Color(0xFFB7B9C2),
        textDisabled = Color(0xFF707384),
        primary = Color(0xFF7FA1F2),
        primaryPressed = Color(0xFF9DB7F5),
        onPrimary = Ink,
        primaryContainer = Color(0xFF1B2B63),
        onPrimaryContainer = Lavender,
        success = Color(0xFF8DB879),
        successText = Color(0xFF8DB879),
        successContainer = Color(0xFF22331C),
        error = Color(0xFFF2837B),
        errorContainer = Color(0xFF3B1E1E),
        warning = Color(0xFFF0B429),
        warningContainer = Color(0xFF3A2E12),
        ink = Ink,
        navy = Navy,
        isDark = true,
    )
