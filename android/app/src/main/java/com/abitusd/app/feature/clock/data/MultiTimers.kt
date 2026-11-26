package com.abitusd.app.feature.clock.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class NamedTimer(
    val id: String,
    val name: String,
    val endsAt: Long,
)

/**
 * Several named countdowns at once (11e), independent of the main clock. Each one is an exact
 * alarm, so they ring with the app closed; the list lives in Room with the clock state.
 */
@Singleton
class MultiTimers
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
        private val store: ClockStore,
    ) {
        private val alarms = context.getSystemService(AlarmManager::class.java)
        private val json = Json { ignoreUnknownKeys = true }
        private val mutable = MutableStateFlow(read())
        val all: StateFlow<List<NamedTimer>> = mutable.asStateFlow()

        private fun read(): List<NamedTimer> =
            store.raw(KEY)?.let { runCatching { json.decodeFromString<List<NamedTimer>>(it) }.getOrNull() }.orEmpty()

        private fun write(list: List<NamedTimer>) {
            store.putRaw(KEY, json.encodeToString(list))
            mutable.value = list
        }

        fun add(
            name: String,
            seconds: Int,
        ) {
            val timer = NamedTimer(UUID.randomUUID().toString(), name, System.currentTimeMillis() + seconds * MS)
            write(read() + timer)
            val intent = pending(timer)
            if (AlarmManagerCompat.canScheduleExactAlarms(alarms)) {
                AlarmManagerCompat.setExactAndAllowWhileIdle(alarms, AlarmManager.RTC_WAKEUP, timer.endsAt, intent)
            } else {
                AlarmManagerCompat.setAndAllowWhileIdle(alarms, AlarmManager.RTC_WAKEUP, timer.endsAt, intent)
            }
        }

        fun remove(id: String) {
            read().firstOrNull { it.id == id }?.let { alarms.cancel(pending(it)) }
            write(read().filterNot { it.id == id })
        }

        private fun pending(timer: NamedTimer): PendingIntent =
            PendingIntent.getBroadcast(
                context,
                timer.id.hashCode(),
                Intent(context, ClockReceiver::class.java)
                    .setAction(ClockReceiver.ACTION_MULTI)
                    .putExtra(ClockReceiver.EXTRA_NAME, timer.name)
                    .putExtra(ClockReceiver.EXTRA_ID, timer.id),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        private companion object {
            const val KEY = "multi"
            const val MS = 1000L
        }
    }
