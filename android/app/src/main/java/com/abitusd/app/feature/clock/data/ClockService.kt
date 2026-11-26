package com.abitusd.app.feature.clock.data

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.abitusd.app.feature.clock.domain.ClockEngine
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Foreground service kept while a clock is active (10c), so the process stays alive with the
 * screen off; the exact alarms still drive every phase change if the system stops it anyway.
 */
@AndroidEntryPoint
class ClockService : Service() {
    @Inject lateinit var store: ClockStore

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val now = System.currentTimeMillis()
        val run = store.refresh()
        val status = run?.let { ClockEngine.status(it, now) }
        if (run == null || status == null || status.done) {
            stopSelf()
            return START_NOT_STICKY
        }
        val type =
            if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.UPSIDE_DOWN_CAKE
            ) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                0
            }
        ServiceCompat.startForeground(
            this,
            ClockNotifications.ID,
            ClockNotifications.build(this, run, status, now),
            type,
        )
        return START_NOT_STICKY
    }

    companion object {
        /** Starts or refreshes the service; ignored where the system forbids a start from the background. */
        fun sync(
            context: Context,
            active: Boolean,
        ) {
            val intent = Intent(context, ClockService::class.java)
            if (!active) {
                context.stopService(intent)
                return
            }
            runCatching { ContextCompat.startForegroundService(context, intent) }
        }
    }
}
