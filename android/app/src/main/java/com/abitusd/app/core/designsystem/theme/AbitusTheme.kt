package com.abitusd.app.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalAppColors = staticCompositionLocalOf { LightAppColors }
private val LocalAppTypography = staticCompositionLocalOf { DefaultAppTypography }

/** Access point to the design tokens from any composable. */
object AppTheme {
    val colors: AppColors
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current

    val typography: AppTypography
        @Composable @ReadOnlyComposable
        get() = LocalAppTypography.current
}

/** Light theme by default; dark derived from ink (05b). Material 3 receives the same tokens. */
@Composable
fun AbitusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkAppColors else LightAppColors
    val base = if (darkTheme) darkColorScheme() else lightColorScheme()
    val material =
        base.copy(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            primaryContainer = colors.primaryContainer,
            onPrimaryContainer = colors.onPrimaryContainer,
            background = colors.background,
            onBackground = colors.textPrimary,
            surface = colors.surface,
            onSurface = colors.textPrimary,
            surfaceVariant = colors.surfaceSoft,
            onSurfaceVariant = colors.textSecondary,
            outline = colors.inputBorder,
            outlineVariant = colors.outline,
            error = colors.error,
            errorContainer = colors.errorContainer,
        )
    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppTypography provides DefaultAppTypography,
    ) {
        MaterialTheme(colorScheme = material, content = content)
    }
}
