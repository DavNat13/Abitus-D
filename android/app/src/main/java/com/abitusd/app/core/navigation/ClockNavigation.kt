package com.abitusd.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.abitusd.app.feature.clock.ui.ClockScreen
import com.abitusd.app.feature.clock.ui.GlossaryScreen

/** Clock (11e) and glossary (11f), reachable from Home, the active session and every info sheet. */
fun EntryProviderScope<NavKey>.clockEntries(back: () -> Unit) {
    entry<ClockRoute> { ClockScreen(onBack = back) }
    entry<GlossaryRoute> { GlossaryScreen(onBack = back) }
}

/** Opens the clock for a launcher shortcut; the stopwatch shortcut also starts it. */
@Composable
fun ShortcutHandler(openClock: () -> Unit) {
    val clock: com.abitusd.app.feature.clock.ui.ClockViewModel = hiltViewModel()
    val pending by AppShortcuts.pending.collectAsStateWithLifecycle()
    val name =
        androidx.compose.ui.res
            .stringResource(com.abitusd.app.R.string.clock_preset_stopwatch)
    LaunchedEffect(pending) {
        val action = pending ?: return@LaunchedEffect
        if (action == AppShortcuts.ACTION_STOPWATCH) clock.startStopwatch(name)
        openClock()
        AppShortcuts.consume()
    }
}
