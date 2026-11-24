package com.abitusd.app.core.timer

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.abitusd.app.MainActivity
import com.abitusd.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reminder at the planned time of a workout (10f Fase 4b). An inexact alarm is enough here, so no
 * exact-alarm permission is used; a reminder in the past is not set.
 */
@Singleton
class WorkoutReminders
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        fun schedule(
            id: String,
            date: LocalDate,
            time: String?,
            title: String,
        ) {
            val manager = context.getSystemService(AlarmManager::class.java)
            val pending = pending(id, title)
            manager.cancel(pending)
            val at =
                time?.let {
                    date
                        .atTime(
                            LocalTime.parse(it),
                        ).atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                }
            if (at != null && at > System.currentTimeMillis()) {
                manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pending)
            }
        }

        private fun pending(
            id: String,
            title: String,
        ): PendingIntent =
            PendingIntent.getBroadcast(
                context,
                id.hashCode(),
                Intent(context, WorkoutReminderReceiver::class.java).putExtra(EXTRA_TITLE, title),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        companion object {
            const val EXTRA_TITLE = "title"
            private const val CHANNEL = "workout_reminder"
            private const val NOTIFICATION_ID = 4201

            fun createChannel(context: Context) {
                context.getSystemService(NotificationManager::class.java).createNotificationChannel(
                    NotificationChannel(
                        CHANNEL,
                        context.getString(R.string.reminder_channel),
                        NotificationManager.IMPORTANCE_DEFAULT,
                    ),
                )
            }

            fun show(
                context: Context,
                title: String,
            ) {
                val granted =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED
                if (!granted) return
                val open =
                    PendingIntent.getActivity(
                        context,
                        0,
                        Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_IMMUTABLE,
                    )
                val notification =
                    NotificationCompat
                        .Builder(context, CHANNEL)
                        .setSmallIcon(R.drawable.ic_timer)
                        .setContentTitle(context.getString(R.string.reminder_title))
                        .setContentText(title)
                        .setAutoCancel(true)
                        .setContentIntent(open)
                        .build()
                NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
            }
        }
    }

/** Posts the reminder when its alarm fires. */
class WorkoutReminderReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        WorkoutReminders.show(context, intent.getStringExtra(WorkoutReminders.EXTRA_TITLE).orEmpty())
    }
}
