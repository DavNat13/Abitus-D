package com.abitusd.app.feature.clock.data

import android.content.Context
import androidx.core.content.edit
import com.abitusd.app.feature.clock.domain.ClockRun
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/** The active clock, persisted on disk so it is recovered after closing the app or rebooting. */
@Singleton
class ClockStore
    @Inject
    constructor(
        @param:ApplicationContext context: Context,
    ) {
        private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        private val json = Json { ignoreUnknownKeys = true }
        private val mutable = MutableStateFlow(read())
        val run: StateFlow<ClockRun?> = mutable.asStateFlow()

        private fun read(): ClockRun? =
            prefs.getString(KEY, null)?.let { runCatching { json.decodeFromString<ClockRun>(it) }.getOrNull() }

        /** Re-reads disk: a receiver in another component may have changed it. */
        fun refresh(): ClockRun? = read().also { mutable.value = it }

        fun set(value: ClockRun?) {
            prefs.edit { if (value == null) remove(KEY) else putString(KEY, json.encodeToString(value)) }
            mutable.value = value
        }

        /** Finished stopwatch runs with laps, newest first, kept on this phone only (11e). */
        fun history(): List<LapRecord> =
            prefs
                .getString(
                    HISTORY,
                    null,
                )?.let { runCatching { json.decodeFromString<List<LapRecord>>(it) }.getOrNull() }
                .orEmpty()

        fun addHistory(record: LapRecord) {
            prefs.edit { putString(HISTORY, json.encodeToString((listOf(record) + history()).take(MAX_HISTORY))) }
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
