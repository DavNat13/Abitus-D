package com.abitusd.app.feature.clock.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.abitusd.app.feature.clock.domain.ClockActions
import com.abitusd.app.feature.clock.domain.ClockEngine
import com.abitusd.app.feature.clock.domain.ClockMode
import com.abitusd.app.feature.clock.domain.ClockRun

/**
 * Volume buttons while the clock screen shows a running clock (11e, control sin mirar):
 * up pauses or resumes; down marks a lap, counts an AMRAP round or skips the phase.
 */
object ClockKeys {
    var onVolumeUp: (() -> Unit)? = null
    var onVolumeDown: (() -> Unit)? = null
}

@Composable
fun ClockKeysEffect(
    run: ClockRun,
    countsUp: Boolean,
    vm: ClockViewModel,
) {
    DisposableEffect(run.pausedAt == null, countsUp, run.mode) {
        ClockKeys.onVolumeUp = { vm.act(if (run.pausedAt == null) ClockEngine::pause else ClockEngine::resume) }
        ClockKeys.onVolumeDown = {
            when {
                countsUp -> vm.act(ClockActions::lap)
                run.mode == ClockMode.AMRAP.key -> vm.act(ClockActions::count)
                else -> vm.act(ClockEngine::skip)
            }
        }
        onDispose {
            ClockKeys.onVolumeUp = null
            ClockKeys.onVolumeDown = null
        }
    }
}

/** Voice commands mapped to the same actions as the buttons. */
@Composable
fun ClockVoiceCommands(vm: ClockViewModel) {
    ClockVoiceControl { command ->
        when (command) {
            VoiceCommand.PAUSE -> vm.act(ClockEngine::pause)
            VoiceCommand.RESUME -> vm.act(ClockEngine::resume)
            VoiceCommand.LAP -> vm.act(ClockActions::lap)
            VoiceCommand.SKIP -> vm.act(ClockEngine::skip)
            VoiceCommand.READY -> vm.act(ClockActions::ready)
            VoiceCommand.STOP -> vm.stop()
        }
    }
}
