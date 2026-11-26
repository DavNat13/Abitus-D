package com.abitusd.app.feature.clock.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.abitusd.app.feature.clock.domain.ClockEngine
import com.abitusd.app.feature.clock.domain.ClockRun
import com.abitusd.app.feature.clock.domain.PhaseKind
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single entry point of the clock (09c): every change is saved, then the next phase change and
 * the 10 s rest warning are set as exact alarms and the live notification is redrawn. As with the
 * rest timer (02h), no foreground service is needed because alarms fire with the process dead.
 */
@Singleton
class ClockController
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
        val store: ClockStore,
        private val records: ClockRecords,
    ) {
        private val alarms = context.getSystemService(AlarmManager::class.java)

        fun start(run: ClockRun) = apply(run)

        fun update(change: (ClockRun, Long) -> ClockRun) {
            val run = store.refresh() ?: return
            apply(change(run, System.currentTimeMillis()))
        }

        fun stop() {
            store
                .refresh()
                ?.takeIf {
                    it.lapsMs.isNotEmpty()
                }?.let { records.addHistory(LapRecord(it.name, it.startedAt, it.lapsMs)) }
            cancelAlarms()
            ClockNotifications.cancel(context)
            store.set(null)
            ClockService.sync(context, active = false)
        }

        /** Called after an alarm or a reboot: re-arms whatever is still pending. */
        fun rearm() {
            store.refresh()?.let { apply(it) }
        }

        private fun apply(run: ClockRun) {
            store.set(run)
            cancelAlarms()
            val now = System.currentTimeMillis()
            val status = ClockEngine.status(run, now)
            ClockNotifications.show(context, run, status, now)
            ClockService.sync(context, active = !status.done)
            if (run.pausedAt != null) return
            val boundary = status.boundary(now) ?: return
            set(boundary, ClockReceiver.ACTION_PHASE, REQUEST_PHASE)
            val warnAt = boundary - WARN_MS
            if (status.kind == PhaseKind.REST && warnAt > now) set(warnAt, ClockReceiver.ACTION_WARN, REQUEST_WARN)
        }

        private fun set(
            at: Long,
            action: String,
            request: Int,
        ) {
            val intent = pending(action, request, at)
            if (AlarmManagerCompat.canScheduleExactAlarms(alarms)) {
                AlarmManagerCompat.setExactAndAllowWhileIdle(alarms, AlarmManager.RTC_WAKEUP, at, intent)
            } else {
                AlarmManagerCompat.setAndAllowWhileIdle(alarms, AlarmManager.RTC_WAKEUP, at, intent)
            }
        }

        private fun cancelAlarms() {
            alarms.cancel(pending(ClockReceiver.ACTION_PHASE, REQUEST_PHASE, 0))
            alarms.cancel(pending(ClockReceiver.ACTION_WARN, REQUEST_WARN, 0))
        }

        private fun pending(
            action: String,
            request: Int,
            at: Long,
        ): PendingIntent =
            PendingIntent.getBroadcast(
                context,
                request,
                Intent(context, ClockReceiver::class.java).setAction(action).putExtra(ClockReceiver.EXTRA_AT, at),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        private companion object {
            const val REQUEST_PHASE = 61
            const val REQUEST_WARN = 62
            const val WARN_MS = 10_000L
        }
    }
