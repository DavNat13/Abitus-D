package com.abitusd.app.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Card with a section title and content (GroupCard of 05d). */
@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    AppCard(modifier) {
        Column(Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                icon?.let { IconBadge(it, size = AppSizes.headerBadge) }
                Text(
                    title,
                    style = AppTheme.typography.titleMedium,
                    color = AppTheme.colors.textPrimary,
                    modifier =
                        Modifier
                            .semantics {
                                heading()
                            },
                )
            }
            content()
        }
    }
}

/** Small non-interactive tag: metadata, muscles, equipment. */
@Composable
fun InfoPill(
    text: String,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
) {
    val colors = AppTheme.colors
    Text(
        text,
        style = AppTheme.typography.label,
        color = if (emphasized) colors.onPrimaryContainer else colors.textPrimary,
        modifier =
            modifier
                .background(if (emphasized) colors.primaryContainer else colors.surfaceSoft, AppShapes.pill)
                .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xxs),
    )
}

/** Numbered step: number in a soft circle, then the text. */
@Composable
fun NumberedStep(
    number: Int,
    text: String,
) {
    val colors = AppTheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        Box(
            Modifier.size(AppSizes.stepBadge).background(colors.primaryContainer, AppShapes.pill),
            contentAlignment = Alignment.Center,
        ) {
            Text(number.toString(), style = AppTheme.typography.label, color = colors.onPrimaryContainer)
        }
        Text(text, style = AppTheme.typography.body, color = colors.textPrimary, modifier = Modifier.weight(1f))
    }
}

/** Row with a small leading icon, used instead of text bullets. */
@Composable
fun IconRow(
    @DrawableRes icon: Int,
    text: String,
    tint: Color = AppTheme.colors.primary,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.padding(top = AppSpacing.xxxs).size(AppSizes.iconSmall),
        )
        Text(
            text,
            style = AppTheme.typography.body,
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}
