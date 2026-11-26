package com.abitusd.app.core.timer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.abitusd.app.MainActivity
import com.abitusd.app.R

/** Rest notifications: a silent ongoing countdown and a loud "rest is over". */
object RestNotifications {
    const val COUNTDOWN_ID = 4101
    private const val DONE_ID = 4102
    private const val CHANNEL_COUNTDOWN = "rest_countdown"
    private const val CHANNEL_DONE = "rest_done"

    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_COUNTDOWN,
                context.getString(R.string.rest_channel_countdown),
                NotificationManager.IMPORTANCE_LOW,
            ),
        )
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_DONE,
                context.getString(R.string.rest_channel_done),
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                enableVibration(true)
            },
        )
    }

    fun showCountdown(
        context: Context,
        endsAt: Long,
        title: String,
    ) {
        val notification =
            NotificationCompat
                .Builder(context, CHANNEL_COUNTDOWN)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(context.getString(R.string.rest_running))
                .setContentText(title)
                .setWhen(endsAt)
                .setUsesChronometer(true)
                .setChronometerCountDown(true)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(openApp(context))
                .addAction(0, context.getString(R.string.rest_add_15), action(context, RestAlarmReceiver.ACTION_ADD))
                .addAction(0, context.getString(R.string.rest_skip), action(context, RestAlarmReceiver.ACTION_SKIP))
                .build()
        post(context, COUNTDOWN_ID, notification)
    }

    fun showDone(context: Context) {
        NotificationManagerCompat.from(context).cancel(COUNTDOWN_ID)
        val notification =
            NotificationCompat
                .Builder(context, CHANNEL_DONE)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(context.getString(R.string.rest_done))
                .setContentText(context.getString(R.string.rest_done_text))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(openApp(context))
                .build()
        post(context, DONE_ID, notification)
    }

    private fun post(
        context: Context,
        id: Int,
        notification: android.app.Notification,
    ) {
        val granted =
            canNotify(context)
        if (granted) NotificationManagerCompat.from(context).notify(id, notification)
    }

    private fun openApp(context: Context): PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE,
        )

    private fun action(
        context: Context,
        name: String,
    ): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            name.hashCode(),
            Intent(context, RestAlarmReceiver::class.java).setAction(name),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}
