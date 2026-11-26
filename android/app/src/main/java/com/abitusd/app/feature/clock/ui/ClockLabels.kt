package com.abitusd.app.feature.clock.ui

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.theme.AppColors
import com.abitusd.app.feature.clock.domain.PhaseKind

object ClockLabels {
    @StringRes
    fun kind(kind: String): Int =
        when (kind) {
            PhaseKind.REST -> R.string.clock_kind_rest
            PhaseKind.PREPARE -> R.string.clock_kind_prepare
            PhaseKind.INHALE -> R.string.clock_kind_inhale
            PhaseKind.HOLD -> R.string.clock_kind_hold
            PhaseKind.EXHALE -> R.string.clock_kind_exhale
            else -> R.string.clock_kind_work
        }

    /** Phase color, always shown with its label so color is never the only signal. */
    fun color(
        kind: String,
        colors: AppColors,
    ): Color =
        when (kind) {
            PhaseKind.REST, PhaseKind.EXHALE -> colors.success
            PhaseKind.HOLD -> colors.textSecondary
            PhaseKind.PREPARE -> colors.warning
            else -> colors.primary
        }

    private val PRESETS =
        mapOf(
            "stopwatch" to R.string.clock_preset_stopwatch,
            "countdown" to R.string.clock_preset_countdown,
            "intervals" to R.string.clock_preset_intervals,
            "tabata" to R.string.clock_preset_tabata,
            "emom" to R.string.clock_preset_emom,
            "e2mom" to R.string.clock_preset_e2mom,
            "amrap" to R.string.clock_preset_amrap,
            "for_time" to R.string.clock_preset_for_time,
            "pyramid" to R.string.clock_preset_pyramid,
            "boxing" to R.string.clock_preset_boxing,
            "mobility" to R.string.clock_preset_mobility,
            "pool" to R.string.clock_preset_pool,
            "pomodoro" to R.string.clock_preset_pomodoro,
            "cooking" to R.string.clock_preset_cooking,
            "pool_send" to R.string.clock_preset_pool_send,
            "track" to R.string.clock_preset_track,
            "box_breathing" to R.string.clock_preset_box_breathing,
            "breathing_478" to R.string.clock_preset_breathing_478,
            "cold" to R.string.clock_preset_cold,
            "sauna" to R.string.clock_preset_sauna,
        )

    @StringRes
    fun preset(key: String): Int = PRESETS[key] ?: R.string.clock_preset_countdown

    /** mm:ss, or h:mm:ss from one hour; tenths for the stopwatch. */
    fun time(
        ms: Long,
        tenths: Boolean = false,
    ): String {
        val total = ms / MS
        val h = total / HOUR
        val m = total % HOUR / MIN
        val s = total % MIN
        val base = if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
        return if (tenths) base + ".${ms % MS / TENTH}" else base
    }

    private const val MS = 1000L
    private const val TENTH = 100L
    private const val MIN = 60L
    private const val HOUR = 3600L
}
