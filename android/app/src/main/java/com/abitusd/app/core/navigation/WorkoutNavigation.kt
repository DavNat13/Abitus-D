package com.abitusd.app.core.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.abitusd.app.feature.workout.ui.active.ActiveSessionScreen
import com.abitusd.app.feature.workout.ui.history.HistoryScreen
import com.abitusd.app.feature.workout.ui.preview.PreSessionScreen
import com.abitusd.app.feature.workout.ui.summary.SummaryScreen

/** Workout flow (10f): preview, active session, summary and history, on the signed-in stack. */
fun EntryProviderScope<NavKey>.workoutEntries(
    stack: NavBackStack<NavKey>,
    back: () -> Unit,
) {
    entry<PreSessionRoute> { key ->
        PreSessionScreen(key.routineId, key.scheduledWorkoutId, onBack = back, onStarted = {
            stack.removeLastOrNull()
            stack.add(ActiveSessionRoute)
        })
    }
    entry<ActiveSessionRoute> {
        ActiveSessionScreen(onBack = back, onFinish = { id -> stack.add(SessionSummaryRoute(id)) })
    }
    entry<SessionSummaryRoute> { key ->
        SummaryScreen(key.sessionId, key.finished, onBack = back, onRepeated = {
            stack.removeAll { it is SessionSummaryRoute || it is HistoryRoute }
            stack.add(ActiveSessionRoute)
        }, onDone = {
            if (key.finished) {
                back()
            } else {
                stack.removeAll { it is SessionSummaryRoute || it is ActiveSessionRoute }
            }
        })
    }
    entry<HistoryRoute> {
        HistoryScreen(onBack = back, onOpen = { id -> stack.add(SessionSummaryRoute(id, finished = true)) })
    }
}
