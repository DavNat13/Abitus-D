package com.abitusd.app.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.abitusd.app.core.designsystem.motion.pressScale
import com.abitusd.app.core.designsystem.theme.AppElevation
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.core.designsystem.theme.appShadow

/** Standard card of 05c: surface, thin outline, radius 16, tinted shadow; pressable when [onClick] is set. */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = AppTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val level = if (pressed) AppElevation.Md else AppElevation.Sm
    val cardColors = CardDefaults.cardColors(containerColor = colors.surface, contentColor = colors.textPrimary)
    val border = BorderStroke(AppSizes.borderThin, colors.outline)
    val base = modifier.fillMaxWidth().pressScale(interaction).appShadow(level, AppShapes.card)
    if (onClick == null) {
        Card(base, AppShapes.card, cardColors, border = border, content = content)
    } else {
        Card(
            onClick,
            base,
            shape = AppShapes.card,
            colors = cardColors,
            border = border,
            interactionSource = interaction,
            content = content,
        )
    }
}
