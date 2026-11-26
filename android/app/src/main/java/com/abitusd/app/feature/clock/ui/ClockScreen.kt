package com.abitusd.app.feature.clock.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.InfoButton
import com.abitusd.app.core.designsystem.component.InfoEntry
import com.abitusd.app.core.designsystem.icon.AppIcons

/** Clock (11e): one engine for every mode; the screen stays on while it runs. */
@Composable
fun ClockScreen(
    onBack: () -> Unit,
    viewModel: ClockViewModel = hiltViewModel(),
    extras: ClockExtrasViewModel = hiltViewModel(),
) {
    var theme by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(extras.store.theme) }
    val s by viewModel.state.collectAsStateWithLifecycle()
    val run = s.run
    val status = s.status
    val view = LocalView.current
    val running = s.keepOn && run != null && run.pausedAt == null && status?.done == false
    DisposableEffect(running) {
        view.keepScreenOn = running
        onDispose { view.keepScreenOn = false }
    }
    ClockVoice(status, s.silent)
    DisposableEffect(Unit) { onDispose { ClockPending.label = null } }
    NotificationRequest()
    FormScreen(title = stringResource(R.string.clock_title), onBack = onBack, info = {
        InfoButton(R.string.clock_title, INFO)
    }) {
        if (run != null && status != null) {
            ClockDisplay(run, status, viewModel, theme)
        } else {
            ClockSetup(s, viewModel)
            ClockShare(s.draft)
            MetronomeCard()
            ClockExtras()
            MultiTimerCard()
            HoldCard()
            FieldTestCard()
        }
        val signals =
            listOf(
                ChoiceOption(false, stringResource(R.string.clock_signals_sound)),
                ChoiceOption(true, stringResource(R.string.clock_signals_silent)),
            )
        AppDropdown(
            stringResource(R.string.clock_signals),
            signals,
            s.silent,
            { viewModel.setSignals(it == true, s.keepOn) },
        )
        val screen =
            listOf(
                ChoiceOption(false, stringResource(R.string.clock_screen_normal)),
                ChoiceOption(true, stringResource(R.string.clock_screen_on)),
            )
        AppDropdown(
            stringResource(R.string.clock_screen),
            screen,
            s.keepOn,
            { viewModel.setSignals(s.silent, it == true) },
        )
    }
}

private val INFO =
    listOf(
        InfoEntry(AppIcons.Timer, R.string.clock_info_modes, R.string.clock_info_modes_d),
        InfoEntry(AppIcons.Pause, R.string.clock_info_background, R.string.clock_info_background_d),
        InfoEntry(AppIcons.Favorite, R.string.clock_info_profiles, R.string.clock_info_profiles_d),
    )

/** Android 13+ asks once for notifications so the live clock is visible with the screen off. */
@Composable
private fun NotificationRequest() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val launcher =
        androidx.activity.compose.rememberLauncherForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts
                .RequestPermission(),
        ) {}
    androidx.compose.runtime.LaunchedEffect(Unit) {
        val modern = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
        if (modern &&
            !com.abitusd.app.core.timer
                .canNotify(context)
        ) {
            launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

/** Pause on calls: turning it on asks for the phone state permission first. */
@Composable
private fun CallPauseSetting(viewModel: ClockViewModel) {
    var on by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(viewModel.pauseOnCall) }
    val ask =
        androidx.activity.compose.rememberLauncherForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts
                .RequestPermission(),
        ) { granted ->
            on = granted
            viewModel.pauseOnCall = granted
        }
    val options =
        listOf(
            ChoiceOption(false, stringResource(R.string.clock_call_off)),
            ChoiceOption(true, stringResource(R.string.clock_call_on)),
        )
    AppDropdown(stringResource(R.string.clock_call), options, on, { value ->
        if (value == true) {
            ask.launch(android.Manifest.permission.READ_PHONE_STATE)
        } else {
            on = false
            viewModel.pauseOnCall = false
        }
    })
}
