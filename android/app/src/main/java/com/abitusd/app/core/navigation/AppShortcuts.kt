package com.abitusd.app.core.navigation

import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Launcher shortcuts and the quick settings tile (11e): the activity records the target screen. */
object AppShortcuts {
    const val ACTION_CLOCK = "com.abitusd.app.OPEN_CLOCK"
    const val ACTION_STOPWATCH = "com.abitusd.app.START_STOPWATCH"

    private val mutable = MutableStateFlow<String?>(null)
    val pending: StateFlow<String?> = mutable.asStateFlow()

    fun receive(intent: Intent?) {
        intent?.action?.takeIf { it == ACTION_CLOCK || it == ACTION_STOPWATCH }?.let { mutable.value = it }
    }

    fun consume() {
        mutable.value = null
    }
}
