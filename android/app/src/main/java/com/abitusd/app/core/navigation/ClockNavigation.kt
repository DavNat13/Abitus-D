package com.abitusd.app.core.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.abitusd.app.feature.clock.ui.ClockScreen
import com.abitusd.app.feature.clock.ui.GlossaryScreen

/** Clock (11e) and glossary (11f), reachable from Home, the active session and every info sheet. */
fun EntryProviderScope<NavKey>.clockEntries(back: () -> Unit) {
    entry<ClockRoute> { ClockScreen(onBack = back) }
    entry<GlossaryRoute> { GlossaryScreen(onBack = back) }
}
