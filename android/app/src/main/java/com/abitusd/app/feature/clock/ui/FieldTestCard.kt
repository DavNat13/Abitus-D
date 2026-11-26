package com.abitusd.app.feature.clock.ui

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.SystemClock
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.clock.domain.FieldTest
import com.abitusd.app.feature.clock.domain.FieldTests
import com.abitusd.app.feature.clock.domain.TestBeep
import kotlinx.coroutines.delay

/** Field tests with beeps made by the app (11f). The screen stays on while a test runs. */
@Composable
fun FieldTestCard() {
    var test by rememberSaveable { mutableStateOf(FieldTest.SHUTTLE_RUN) }
    var running by rememberSaveable { mutableStateOf(false) }
    var last by remember { mutableStateOf<TestBeep?>(null) }
    var elapsed by remember { mutableLongStateOf(0L) }
    var result by rememberSaveable { mutableStateOf<String?>(null) }
    FieldTestRunner(test, running, { last = it }, { elapsed = it }) { running = false }
    val names =
        mapOf(
            FieldTest.SHUTTLE_RUN to R.string.test_shuttle,
            FieldTest.YO_YO_IR1 to R.string.test_yoyo,
            FieldTest.SPRINT_30 to R.string.test_sprint,
        )
    val sprintTime = ClockLabels.time((elapsed - SPRINT_START_MS).coerceAtLeast(0), tenths = true)
    SectionCard(stringResource(R.string.test_title), icon = AppIcons.Timer) {
        AppDropdown(
            stringResource(R.string.test_choose),
            FieldTest.entries.map {
                ChoiceOption(it, stringResource(names.getValue(it)))
            },
            test,
            {
                if (!running && it != null) test = it
            },
        )
        Text(
            stringResource(R.string.test_note),
            style = AppTheme.typography.body,
            color = AppTheme.colors.textSecondary,
        )
        val now = last
        TestLive(test, running, now, sprintTime)
        result?.let { Text(it, style = AppTheme.typography.titleMedium, color = AppTheme.colors.primary) }
        val stopText = stringResource(R.string.test_stop)
        val reached = now?.let { stringResource(R.string.test_result_level, it.level, it.shuttle) }
        val sprint = stringResource(R.string.test_result_time, sprintTime)
        AppButton(if (running) stopText else stringResource(R.string.test_start), {
            if (running) result = if (test == FieldTest.SPRINT_30) sprint else reached
            running = !running
        }, style = if (running) AppButtonStyle.Destructive else AppButtonStyle.Tonal, icon = AppIcons.Start)
    }
}

@Composable
private fun TestLive(
    test: FieldTest,
    running: Boolean,
    now: TestBeep?,
    sprintTime: String,
) {
    if (!running) return
    val big = AppTheme.typography.displayNumeric.copy(fontSize = BIG.sp)
    when {
        test == FieldTest.SPRINT_30 -> Text(sprintTime, style = big)
        now != null -> Text(stringResource(R.string.test_level, now.level, now.shuttle), style = big)
    }
}

/** Plays the beeps against the monotonic clock while [running]; reports each beep and the time. */
@Composable
private fun FieldTestRunner(
    test: FieldTest,
    running: Boolean,
    onBeep: (TestBeep) -> Unit,
    onTick: (Long) -> Unit,
    onEnd: () -> Unit,
) {
    val tone = remember { ToneGenerator(AudioManager.STREAM_NOTIFICATION, VOLUME) }
    val view = LocalView.current
    DisposableEffect(running) {
        view.keepScreenOn = running
        onDispose { view.keepScreenOn = false }
    }
    DisposableEffect(Unit) { onDispose { tone.release() } }
    LaunchedEffect(running, test) {
        if (!running) return@LaunchedEffect
        val start = SystemClock.elapsedRealtime()

        fun since() = SystemClock.elapsedRealtime() - start
        for (beep in FieldTests.schedule(test)) {
            while (since() < beep.atMs) {
                onTick(since())
                delay(minOf(TICK_MS, beep.atMs - since()).coerceAtLeast(1))
            }
            tone.startTone(if (beep.start) ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_PROP_BEEP, BEEP_MS)
            onBeep(beep)
        }
        while (test == FieldTest.SPRINT_30) {
            onTick(since())
            delay(TICK_MS)
        }
        onEnd()
    }
}

private const val VOLUME = 90
private const val BEEP_MS = 150
private const val TICK_MS = 50L
private const val BIG = 40
private const val SPRINT_START_MS = 3_000L
