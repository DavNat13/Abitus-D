package com.abitusd.app.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.abitusd.app.core.designsystem.motion.AppMotion
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Pill segmented control for the sub-sections of a screen (Routines, Programs, Calendar). */
@Composable
fun SegmentedTabs(
    labels: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .background(colors.surfaceSoft, AppShapes.pill)
            .padding(AppSpacing.xxs)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xxs),
    ) {
        labels.forEachIndexed { index, label ->
            val isSelected = index == selected
            val background by animateColorAsState(
                if (isSelected) colors.surface else colors.surfaceSoft,
                tween(AppMotion.MICRO_MS),
                label = "segment",
            )
            Box(
                Modifier
                    .weight(1f)
                    .heightIn(min = AppSizes.touchTarget)
                    .clip(AppShapes.pill)
                    .background(background, AppShapes.pill)
                    .selectable(isSelected, role = Role.Tab) { onSelect(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = AppTheme.typography.label,
                    color = if (isSelected) colors.primary else colors.textSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
