package com.abitusd.app.core.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.abitusd.app.core.designsystem.motion.AppMotion
import com.abitusd.app.core.designsystem.motion.rememberReduceMotion
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

private const val SHIMMER_MIN = 0.45f
private const val TITLE_FRACTION = 0.6f
private const val META_FRACTION = 0.35f

/** Placeholder list item with a soft 1.2 s pulse (05c, Estados de pantalla). Hidden from screen readers. */
@Composable
fun SkeletonListItem(modifier: Modifier = Modifier) {
    val reduce = rememberReduceMotion()
    val transition = rememberInfiniteTransition(label = "skeleton")
    val pulse by transition.animateFloat(
        SHIMMER_MIN,
        1f,
        infiniteRepeatable(tween(AppMotion.SKELETON_MS / 2, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse",
    )
    val block = AppTheme.colors.surfaceSoft
    AppCard(modifier.clearAndSetSemantics {}.graphicsLayer { alpha = if (reduce) 1f else pulse }) {
        Row(
            Modifier.heightIn(min = AppSizes.listItemMin).padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(AppSizes.badge).background(block, AppShapes.pill))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                Box(
                    Modifier
                        .fillMaxWidth(
                            TITLE_FRACTION,
                        ).height(AppSizes.skeletonLine)
                        .background(block, AppShapes.field),
                )
                Box(
                    Modifier
                        .fillMaxWidth(
                            META_FRACTION,
                        ).height(AppSizes.skeletonLine)
                        .background(block, AppShapes.field),
                )
            }
        }
    }
}
