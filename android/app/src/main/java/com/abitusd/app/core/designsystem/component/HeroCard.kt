package com.abitusd.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.abitusd.app.core.designsystem.theme.AppElevation
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.core.designsystem.theme.LightAppColors
import com.abitusd.app.core.designsystem.theme.appShadow

/**
 * Main card of 05c: brand gradient, radius 24, white text and a white button with primary text.
 * The brand colors are fixed so the white text keeps its contrast in the dark theme too.
 */
@Composable
fun HeroCard(
    overline: String,
    title: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val brand = LightAppColors
    Column(
        modifier
            .fillMaxWidth()
            .appShadow(AppElevation.Md, AppShapes.hero)
            .background(Brush.linearGradient(listOf(brand.navy, brand.primary)), AppShapes.hero)
            .padding(AppSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        Text(overline, style = AppTheme.typography.label, color = brand.onPrimary)
        Text(
            title,
            style = AppTheme.typography.headline,
            color = brand.onPrimary,
            modifier = Modifier.semantics { heading() },
        )
        content()
        Button(
            onClick = onAction,
            modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.xs),
            shape = AppShapes.button,
            colors = ButtonDefaults.buttonColors(containerColor = brand.surface, contentColor = brand.primary),
            contentPadding = ButtonDefaults.ContentPadding,
        ) {
            Text(actionLabel, style = AppTheme.typography.label, modifier = Modifier.padding(vertical = AppSizes.none))
        }
    }
}
