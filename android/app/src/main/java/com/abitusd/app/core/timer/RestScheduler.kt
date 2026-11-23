package com.abitusd.app.core.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Rest between sets (10f, 09c). No foreground service: the end is an exact alarm that fires
 * with the screen off even if the process died, and an ongoing notification shows the system
 * countdown meanwhile. Decision and sources in claude/02h-MEMORIA-FASE-4.md.
 */
@Singleton
class RestScheduler
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val alarms = context.getSystemService(AlarmManager::class.java)

        /** Schedules the end at [endsAt] (wall clock) and shows the countdown notification. */
        fun start(
            endsAt: Long,
            title: String,
        ) {
            if (alarms.canScheduleExactAlarms()) {
                alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endsAt, endIntent())
            } else {
                alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endsAt, endIntent())
            }
            RestNotifications.showCountdown(context, endsAt, title)
        }

        fun cancel() {
            alarms.cancel(endIntent())
            NotificationManagerCompat.from(context).cancel(RestNotifications.COUNTDOWN_ID)
        }

        private fun endIntent(): PendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_END,
                Intent(context, RestAlarmReceiver::class.java).setAction(RestAlarmReceiver.ACTION_END),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        private companion object {
            const val REQUEST_END = 41
        }
    }
