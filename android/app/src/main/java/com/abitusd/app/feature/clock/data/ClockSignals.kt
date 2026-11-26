package com.abitusd.app.feature.clock.data

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.VibrationEffect
import android.os.Vibrator
import com.abitusd.app.feature.clock.domain.PhaseKind

/**
 * Sound and vibration per phase (11e): distinct haptics per kind and short tones on the
 * notification stream, which does not take audio focus, so music keeps playing.
 */
object ClockSignals {
    private const val TONE_MS = 180
    private const val VOLUME = 80
    private val DONE = longArrayOf(0, 400, 150, 400, 150, 400)
    private val REST = longArrayOf(0, 150, 100, 150)
    private val WORK = longArrayOf(0, 500)
    private val WARN = longArrayOf(0, 80)

    fun phase(
        context: Context,
        kind: String?,
        silent: Boolean,
    ) {
        val pattern =
            when (kind) {
                null -> DONE
                PhaseKind.REST -> REST
                else -> WORK
            }
        vibrate(context, pattern)
        if (!silent) tone(if (kind == null) ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_PROP_BEEP2)
    }

    fun warning(
        context: Context,
        silent: Boolean,
    ) {
        vibrate(context, WARN)
        if (!silent) tone(ToneGenerator.TONE_PROP_BEEP)
    }

    private fun vibrate(
        context: Context,
        pattern: LongArray,
    ) {
        val vibrator = context.getSystemService(Vibrator::class.java) ?: return
        if (vibrator.hasVibrator()) vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
    }

    private fun tone(type: Int) {
        runCatching {
            val generator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, VOLUME)
            generator.startTone(type, TONE_MS)
            Thread.sleep(TONE_MS.toLong())
            generator.release()
        }
    }
}
