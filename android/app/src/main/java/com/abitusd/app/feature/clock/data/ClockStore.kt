package com.abitusd.app.feature.clock.data

import android.content.Context
import androidx.core.content.edit
import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.clock.ClockStateEntity
import com.abitusd.app.feature.clock.domain.ClockRun
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The active clock and lap history in Room (`clock_state`), so they are recovered after closing the
 * app or rebooting; small display settings stay in SharedPreferences. Each access is one tiny row,
 * run on the IO dispatcher.
 */
@Singleton
class ClockStore
    @Inject
    constructor(
        @param:ApplicationContext context: Context,
        database: AbitusDatabase,
    ) {
        private val dao = database.clockDao()
        private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        private val json = Json { ignoreUnknownKeys = true }
        private val mutable = MutableStateFlow(read())
        val run: StateFlow<ClockRun?> = mutable.asStateFlow()

        private fun <T> db(block: suspend () -> T): T = runBlocking(Dispatchers.IO) { block() }

        private fun value(key: String): String? = db { dao.state(key) }

        private fun put(
            key: String,
            value: String?,
        ) = db { if (value == null) dao.deleteState(key) else dao.putState(ClockStateEntity(key, value)) }

        private fun read(): ClockRun? =
            value(KEY)?.let { runCatching { json.decodeFromString<ClockRun>(it) }.getOrNull() }

        /** Re-reads disk: a receiver in another component may have changed it. */
        fun refresh(): ClockRun? = read().also { mutable.value = it }

        fun set(value: ClockRun?) {
            put(KEY, value?.let { json.encodeToString(it) })
            mutable.value = value
        }

        /** Finished stopwatch runs with laps, newest first, kept on this phone only (11e). */
        fun history(): List<LapRecord> =
            value(HISTORY)?.let { runCatching { json.decodeFromString<List<LapRecord>>(it) }.getOrNull() }.orEmpty()

        fun addHistory(record: LapRecord) {
            put(HISTORY, json.encodeToString((listOf(record) + history()).take(MAX_HISTORY)))
        }

        /** Isometric holds and skill attempts by name, in ms, newest first (11b, 11c). */
        fun holds(): Map<String, List<Long>> =
            value(
                HOLDS,
            )?.let { runCatching { json.decodeFromString<Map<String, List<Long>>>(it) }.getOrNull() }.orEmpty()

        fun addHold(
            name: String,
            ms: Long,
        ) {
            val all = holds()
            put(HOLDS, json.encodeToString(all + (name to (listOf(ms) + all[name].orEmpty()).take(MAX_HISTORY))))
        }

        /** Removes the active clock and the history on sign out. */
        fun clearAll() {
            db { dao.clearState() }
            mutable.value = null
        }

        /** Deviation of the last phase alarm in ms, shown as the precision self-test. */
        var lastDriftMs: Long
            get() = prefs.getLong(DRIFT, Long.MIN_VALUE)
            set(value) = prefs.edit { putLong(DRIFT, value) }

        /** Target date (ISO) of the countdown to a competition or test. */
        var targetDate: String?
            get() = prefs.getString(TARGET, null)
            set(value) = prefs.edit { putString(TARGET, value) }

        /** Vibration and visible signals only; tones off. */
        var silent: Boolean
            get() = prefs.getBoolean(SILENT, false)
            set(value) = prefs.edit { putBoolean(SILENT, value) }

        /** Keep the screen on while the clock runs; off by default to save battery. */
        var keepOn: Boolean
            get() = prefs.getBoolean(KEEP_ON, false)
            set(value) = prefs.edit { putBoolean(KEEP_ON, value) }

        private companion object {
            const val PREFS = "abitusd_clock"
            const val KEY = "active"
            const val SILENT = "silent"
            const val KEEP_ON = "keep_on"
            const val HISTORY = "history"
            const val HOLDS = "holds"
            const val DRIFT = "drift_ms"
            const val TARGET = "target_date"
            const val MAX_HISTORY = 20
        }
    }

@kotlinx.serialization.Serializable
data class LapRecord(
    val name: String,
    val at: Long,
    val lapsMs: List<Long>,
)
