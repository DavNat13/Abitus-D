package com.abitusd.app.feature.clock.ui

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.Stepper
import com.abitusd.app.core.designsystem.icon.AppIcons
import kotlinx.coroutines.delay

/**
 * Metronome for running cadence or lifting tempo (11e). Beats are scheduled against the monotonic
 * clock, not by adding delays, so they do not drift; it plays while this screen is open.
 */
@Composable
fun MetronomeCard() {
    var bpm by rememberSaveable { mutableIntStateOf(DEFAULT_BPM) }
    var on by rememberSaveable { mutableStateOf(false) }
    val tone = remember { ToneGenerator(AudioManager.STREAM_NOTIFICATION, VOLUME) }
    DisposableEffect(Unit) { onDispose { tone.release() } }
    LaunchedEffect(on, bpm) {
        if (!on) return@LaunchedEffect
        val period = MINUTE_MS / bpm
        var next = SystemClock.elapsedRealtime()
        while (true) {
            tone.startTone(ToneGenerator.TONE_PROP_BEEP, BEEP_MS)
            next += period
            delay(maxOf(0, next - SystemClock.elapsedRealtime()))
        }
    }
    SectionCard(stringResource(R.string.clock_metronome), icon = AppIcons.Timer) {
        Stepper(
            stringResource(R.string.clock_bpm),
            bpm.toString(),
            stringResource(R.string.clock_less),
            stringResource(R.string.clock_more),
            { bpm = maxOf(MIN_BPM, bpm - STEP) },
            { bpm = minOf(MAX_BPM, bpm + STEP) },
        )
        AppButton(
            stringResource(if (on) R.string.clock_metronome_stop else R.string.clock_metronome_start),
            { on = !on },
            style = if (on) AppButtonStyle.Secondary else AppButtonStyle.Tonal,
            icon = if (on) AppIcons.Pause else AppIcons.Start,
        )
    }
}

private const val DEFAULT_BPM = 170
private const val MIN_BPM = 30
private const val MAX_BPM = 240
private const val STEP = 5
private const val MINUTE_MS = 60_000L
private const val BEEP_MS = 60
private const val VOLUME = 80
