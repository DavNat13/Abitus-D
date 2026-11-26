package com.abitusd.app.feature.clock.ui

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.feature.clock.domain.ClockStatus

/**
 * Spoken cues while the clock screen is open, with the device's offline voice (11e): phase name,
 * halfway, 10 seconds left and 3-2-1. Phases shorter than [MIN_CUE_MS] only announce their name.
 */
@Composable
fun ClockVoice(
    status: ClockStatus?,
    silent: Boolean,
) {
    val context = LocalContext.current
    val tts = remember { TextToSpeech(context.applicationContext) {} }
    DisposableEffect(Unit) { onDispose { tts.shutdown() } }
    val cue = status?.let(::cueOf)
    val phrase =
        when (cue) {
            null -> {
                null
            }

            Cue.DONE -> {
                stringResource(R.string.clock_done)
            }

            Cue.HALF -> {
                stringResource(R.string.clock_cue_half)
            }

            Cue.TEN -> {
                stringResource(R.string.clock_cue_ten) +
                    (status.nextLabel?.let { ". " + stringResource(R.string.clock_cue_next, it) } ?: "")
            }

            Cue.THREE -> {
                "3"
            }

            Cue.TWO -> {
                "2"
            }

            Cue.ONE -> {
                "1"
            }

            Cue.PHASE -> {
                listOfNotNull(stringResource(ClockLabels.kind(status.kind)), status.label).joinToString(". ")
            }
        }
    LaunchedEffect(status?.index, cue) {
        if (!silent && phrase != null) tts.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, "clock")
    }
}

private enum class Cue { PHASE, HALF, TEN, THREE, TWO, ONE, DONE }

private fun cueOf(s: ClockStatus): Cue {
    val left = s.remainingMs
    val short = left == Long.MAX_VALUE || s.phaseMs < MIN_CUE_MS
    return when {
        s.done -> Cue.DONE
        short -> Cue.PHASE
        left <= ONE_S -> Cue.ONE
        left <= TWO_S -> Cue.TWO
        left <= THREE_S -> Cue.THREE
        left <= TEN_S -> Cue.TEN
        left <= s.phaseMs / 2 -> Cue.HALF
        else -> Cue.PHASE
    }
}

private const val ONE_S = 1_000L
private const val TWO_S = 2_000L
private const val THREE_S = 3_000L
private const val TEN_S = 10_000L
private const val MIN_CUE_MS = 30_000L
