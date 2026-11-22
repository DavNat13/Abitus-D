package com.abitusd.app.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/** Spacing scale on a 4 dp base (05b-UI-TOKENS.md). */
object AppSpacing {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 48.dp
    val screenMargin = 20.dp
}

/** Corner radii and sizes of components (05b and 05c). */
object AppShapes {
    val field = RoundedCornerShape(12.dp)
    val button = RoundedCornerShape(12.dp)
    val card = RoundedCornerShape(16.dp)
    val hero = RoundedCornerShape(24.dp)
    val pill = RoundedCornerShape(percent = 50)
}

object AppSizes {
    val buttonHeight = 52.dp
    val fieldHeight = 56.dp
    val touchTarget = 48.dp
    val icon = 24.dp
    val iconContainer = 64.dp
    val borderThin = 1.dp
    val borderButton = 1.5.dp
    val borderFocus = 2.dp
    val codeBox = 48.dp
    val maxContentWidth = 480.dp
}
