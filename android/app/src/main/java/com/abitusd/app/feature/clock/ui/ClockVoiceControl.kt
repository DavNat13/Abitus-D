package com.abitusd.app.feature.clock.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.icon.AppIcons

/**
 * Hands-free control (11e) with the phone's own recognizer, asking for offline recognition. The
 * button only appears when the phone has a recognizer; it listens again after each command.
 */
@Composable
fun ClockVoiceControl(onCommand: (VoiceCommand) -> Unit) {
    val context = LocalContext.current
    if (!SpeechRecognizer.isRecognitionAvailable(context)) return
    var on by rememberSaveable { mutableStateOf(false) }
    val words =
        VoiceCommand.entries.associateWith { c ->
            stringResource(c.words).split(",").map { it.trim().lowercase() }
        }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { on = it }
    val recognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val intent =
        remember {
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        }
    DisposableEffect(on) {
        recognizer.setRecognitionListener(
            object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    val heard =
                        results
                            ?.getStringArrayList(
                                SpeechRecognizer.RESULTS_RECOGNITION,
                            ).orEmpty()
                            .joinToString(" ")
                            .lowercase()
                    words.entries
                        .firstOrNull { (_, list) ->
                            list.any { heard.contains(it) }
                        }?.let { onCommand(it.key) }
                    if (on) recognizer.startListening(intent)
                }

                override fun onError(error: Int) {
                    if (on) recognizer.startListening(intent)
                }

                override fun onReadyForSpeech(params: Bundle?) = Unit

                override fun onBeginningOfSpeech() = Unit

                override fun onRmsChanged(rmsdB: Float) = Unit

                override fun onBufferReceived(buffer: ByteArray?) = Unit

                override fun onEndOfSpeech() = Unit

                override fun onPartialResults(partialResults: Bundle?) = Unit

                override fun onEvent(
                    eventType: Int,
                    params: Bundle?,
                ) = Unit
            },
        )
        if (on) recognizer.startListening(intent) else recognizer.cancel()
        onDispose { recognizer.cancel() }
    }
    DisposableEffect(Unit) { onDispose { recognizer.destroy() } }
    AppButton(stringResource(if (on) R.string.clock_voice_on else R.string.clock_voice_off), {
        val granted =
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        when {
            on -> on = false
            granted -> on = true
            else -> permission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }, style = if (on) AppButtonStyle.Tonal else AppButtonStyle.Secondary, icon = AppIcons.Start)
}

enum class VoiceCommand(
    val words: Int,
) {
    PAUSE(R.string.clock_voice_words_pause),
    RESUME(R.string.clock_voice_words_resume),
    LAP(R.string.clock_voice_words_lap),
    SKIP(R.string.clock_voice_words_skip),
    READY(R.string.clock_voice_words_ready),
    STOP(R.string.clock_voice_words_stop),
}
