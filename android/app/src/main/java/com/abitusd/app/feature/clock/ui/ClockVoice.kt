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

/** Spoken phase name while the clock screen is open, with the device's offline voice; off in silent mode. */
@Composable
fun ClockVoice(
    status: ClockStatus?,
    silent: Boolean,
) {
    val context = LocalContext.current
    val tts = remember { TextToSpeech(context.applicationContext) {} }
    DisposableEffect(Unit) { onDispose { tts.shutdown() } }
    val phrase =
        when {
            status == null -> null
            status.done -> stringResource(R.string.clock_done)
            else -> stringResource(ClockLabels.kind(status.kind))
        }
    LaunchedEffect(status?.index, status?.done) {
        if (!silent && phrase != null) tts.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, "clock")
    }
}
