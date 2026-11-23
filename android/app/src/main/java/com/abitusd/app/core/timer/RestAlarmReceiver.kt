package com.abitusd.app.core.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.abitusd.app.core.database.AbitusDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Rest end alarm and notification actions (+15 s, skip). Works without the app open. */
@AndroidEntryPoint
class RestAlarmReceiver : BroadcastReceiver() {
    @Inject lateinit var database: AbitusDatabase

    @Inject lateinit var scheduler: RestScheduler

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                handle(context, intent.action)
            } finally {
                pending.finish()
            }
        }
    }

    private suspend fun handle(
        context: Context,
        action: String?,
    ) {
        val dao = database.sessionDao()
        val active = dao.active()
        when (action) {
            ACTION_END -> {
                if (active != null) dao.setActive(active.copy(restEndsAt = null))
                RestNotifications.showDone(context)
            }

            ACTION_SKIP -> {
                scheduler.cancel()
                if (active != null) dao.setActive(active.copy(restEndsAt = null))
            }

            ACTION_ADD -> {
                val endsAt = active?.restEndsAt ?: return
                val next = endsAt + EXTRA_MS
                dao.setActive(active.copy(restEndsAt = next, restTotalS = active.restTotalS + EXTRA_S))
                scheduler.start(next, "")
            }
        }
    }

    companion object {
        const val ACTION_END = "com.abitusd.app.rest.END"
        const val ACTION_SKIP = "com.abitusd.app.rest.SKIP"
        const val ACTION_ADD = "com.abitusd.app.rest.ADD"
        private const val EXTRA_S = 15
        private const val EXTRA_MS = EXTRA_S * 1000L
    }
}
