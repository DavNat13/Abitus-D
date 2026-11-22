package com.abitusd.app.core.designsystem.motion

import android.provider.Settings
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/** Durations and curves of 05c (Movimiento). */
object AppMotion {
    const val MICRO_MS = 120
    const val ELEMENT_MS = 220
    const val SCREEN_MS = 300
    const val SHEET_MS = 250
    const val SKELETON_MS = 1200
    const val STAGGER_MS = 40
    const val STAGGER_MAX_ITEMS = 8
    const val PRESS_SCALE_CARD = 0.98f
    const val PRESS_SCALE_BUTTON = 0.97f

    val Standard = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val Accelerate = CubicBezierEasing(0.3f, 0f, 1f, 1f)
}

/** True when the system animator scale is 0 (animations off): screens skip motion. */
@Composable
fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}
