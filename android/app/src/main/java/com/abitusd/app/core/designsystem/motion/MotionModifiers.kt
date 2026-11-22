package com.abitusd.app.core.designsystem.motion

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/** Scales the element down while pressed (05c: short spring). */
@Composable
fun Modifier.pressScale(
    interaction: InteractionSource,
    pressed: Float = AppMotion.PRESS_SCALE_CARD,
): Modifier {
    val reduce = rememberReduceMotion()
    val isPressed by interaction.collectIsPressedAsState()
    val target = if (isPressed && !reduce) pressed else 1f
    val scale by animateFloatAsState(target, spring(stiffness = Spring.StiffnessMedium), label = "press")
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/** Fades and lifts the first list items in once, 40 ms apart (05c). */
@Composable
fun Modifier.staggeredEnter(index: Int): Modifier {
    val reduce = rememberReduceMotion()
    if (reduce || index >= AppMotion.STAGGER_MAX_ITEMS) return this
    val progress = remember { Animatable(0f) }
    val offset = with(LocalDensity.current) { ENTER_OFFSET.toPx() }
    LaunchedEffect(Unit) {
        progress.animateTo(
            1f,
            tween(
                AppMotion.ELEMENT_MS,
                delayMillis = index * AppMotion.STAGGER_MS,
                easing = AppMotion.EmphasizedDecelerate,
            ),
        )
    }
    return graphicsLayer {
        alpha = progress.value
        translationY = (1f - progress.value) * offset
    }
}

private val ENTER_OFFSET = 8.dp
