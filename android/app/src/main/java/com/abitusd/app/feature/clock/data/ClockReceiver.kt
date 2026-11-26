package com.abitusd.app.feature.clock.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.timer.WorkoutReminders
import com.abitusd.app.feature.clock.domain.ClockEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/** Phase alarms, notification actions and reboot recovery of the clock and calendar reminders. */
@AndroidEntryPoint
class ClockReceiver : BroadcastReceiver() {
    @Inject lateinit var controller: ClockController

    @Inject lateinit var database: AbitusDatabase

    @Inject lateinit var reminders: WorkoutReminders

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                handle(context, intent)
            } finally {
                pending.finish()
            }
        }
    }

    private suspend fun handle(
        context: Context,
        intent: Intent,
    ) {
        val silent = controller.store.silent
        when (intent.action) {
            ACTION_PHASE -> {
                val expected = intent.getLongExtra(EXTRA_AT, 0)
                Log.i(TAG, "phase drift_ms=${System.currentTimeMillis() - expected}")
                controller.rearm()
                val run = controller.store.run.value ?: return
                val status = ClockEngine.status(run, System.currentTimeMillis())
                ClockSignals.phase(context, if (status.done) null else status.kind, silent)
            }

            ACTION_WARN -> {
                ClockSignals.warning(context, silent)
            }

            ACTION_PAUSE -> {
                controller.update(ClockEngine::pause)
            }

            ACTION_RESUME -> {
                controller.update(ClockEngine::resume)
            }

            ACTION_ADD -> {
                controller.update { run, now -> ClockEngine.addTime(run, now, EXTRA_S) }
            }

            ACTION_SKIP -> {
                controller.update(ClockEngine::skip)
            }

            ACTION_LAP -> {
                controller.update(ClockEngine::lap)
            }

            ACTION_STOP -> {
                controller.stop()
            }

            ACTION_RECOVER -> {
                recover()
            }
        }
    }

    private suspend fun recover() {
        controller.rearm()
        database.clockDao().upcomingReminders(LocalDate.now().toString()).forEach {
            reminders.schedule(it.id, LocalDate.parse(it.date), it.time, it.title)
        }
    }

    companion object {
        const val ACTION_PHASE = "com.abitusd.app.clock.PHASE"
        const val ACTION_WARN = "com.abitusd.app.clock.WARN"
        const val ACTION_PAUSE = "com.abitusd.app.clock.PAUSE"
        const val ACTION_RESUME = "com.abitusd.app.clock.RESUME"
        const val ACTION_ADD = "com.abitusd.app.clock.ADD"
        const val ACTION_SKIP = "com.abitusd.app.clock.SKIP"
        const val ACTION_LAP = "com.abitusd.app.clock.LAP"
        const val ACTION_STOP = "com.abitusd.app.clock.STOP"
        const val ACTION_RECOVER = "com.abitusd.app.clock.RECOVER"
        const val EXTRA_AT = "at"
        const val EXTRA_S = 15
        private const val TAG = "AbitusClock"
    }
}
