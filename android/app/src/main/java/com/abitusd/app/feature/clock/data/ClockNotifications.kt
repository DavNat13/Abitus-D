package com.abitusd.app.feature.clock.data

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
import com.abitusd.app.core.timer.canNotify
import com.abitusd.app.feature.clock.domain.ClockMode
import com.abitusd.app.feature.clock.domain.ClockRun
import com.abitusd.app.feature.clock.domain.ClockStatus
import com.abitusd.app.feature.clock.ui.ClockLabels

/** Live clock notification with the system chronometer and its actions (11e). */
object ClockNotifications {
    private const val CHANNEL = "clock_live"
    private const val ID = 7301
    private val LABELS =
        mapOf(
            ClockReceiver.ACTION_RESUME to R.string.clock_resume,
            ClockReceiver.ACTION_PAUSE to R.string.clock_pause,
            ClockReceiver.ACTION_LAP to R.string.clock_lap,
            ClockReceiver.ACTION_ADD to R.string.clock_add_15,
            ClockReceiver.ACTION_SKIP to R.string.clock_skip,
            ClockReceiver.ACTION_STOP to R.string.clock_stop,
        )

    fun createChannel(context: Context) {
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(CHANNEL, context.getString(R.string.clock_channel), NotificationManager.IMPORTANCE_LOW),
        )
    }

    fun show(
        context: Context,
        run: ClockRun,
        status: ClockStatus,
        now: Long,
    ) {
        val granted =
            canNotify(context)
        if (!granted) return
        val countsUp = ClockMode.of(run.mode).countsUp
        val paused = run.pausedAt != null
        val text =
            when {
                status.done -> {
                    context.getString(R.string.clock_done)
                }

                paused -> {
                    context.getString(R.string.clock_paused)
                }

                else -> {
                    context.getString(
                        R.string.clock_round_of,
                        context.getString(ClockLabels.kind(status.kind)),
                        status.round,
                        status.rounds,
                    )
                }
            }
        val builder =
            NotificationCompat
                .Builder(context, CHANNEL)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(run.name)
                .setContentText(text)
                .setOnlyAlertOnce(true)
                .setOngoing(!status.done)
                .setContentIntent(open(context))
        if (!paused && !status.done) {
            builder.setUsesChronometer(true)
            if (countsUp) {
                builder.setWhen(now - status.elapsedMs)
            } else {
                builder.setWhen(now + status.remainingMs).setChronometerCountDown(true)
            }
        }
        val actions =
            when {
                status.done -> emptyList()
                countsUp -> listOf(ClockReceiver.ACTION_LAP)
                else -> listOf(ClockReceiver.ACTION_ADD, ClockReceiver.ACTION_SKIP)
            }
        val toggle = if (paused) ClockReceiver.ACTION_RESUME else ClockReceiver.ACTION_PAUSE
        (listOfNotNull(toggle.takeIf { !status.done }) + actions + ClockReceiver.ACTION_STOP).forEach {
            builder.addAction(0, context.getString(LABELS.getValue(it)), action(context, it))
        }
        NotificationManagerCompat.from(context).notify(ID, builder.build())
    }

    fun cancel(context: Context) = NotificationManagerCompat.from(context).cancel(ID)

    private fun open(context: Context): PendingIntent =
        PendingIntent.getActivity(
            context,
            ID,
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
            Intent(context, ClockReceiver::class.java).setAction(name),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}
