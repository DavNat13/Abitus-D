package com.abitusd.app.feature.clock.data

import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/** Local records of the clock: stopwatch lap history and holds, stored with the clock state. */
@Singleton
class ClockRecords
    @Inject
    constructor(
        private val store: ClockStore,
    ) {
        private val json = Json { ignoreUnknownKeys = true }

        /** Finished stopwatch runs with laps, newest first, kept on this phone only (11e). */
        fun history(): List<LapRecord> =
            store.raw(HISTORY)?.let { runCatching { json.decodeFromString<List<LapRecord>>(it) }.getOrNull() }.orEmpty()

        fun addHistory(record: LapRecord) {
            store.putRaw(HISTORY, json.encodeToString((listOf(record) + history()).take(MAX_HISTORY)))
        }

        /** Isometric holds and skill attempts by name, in ms, newest first (11b, 11c). */
        fun holds(): Map<String, List<Long>> =
            store
                .raw(
                    HOLDS,
                )?.let { runCatching { json.decodeFromString<Map<String, List<Long>>>(it) }.getOrNull() }
                .orEmpty()

        fun addHold(
            name: String,
            ms: Long,
        ) {
            val all = holds()
            store.putRaw(
                HOLDS,
                json.encodeToString(all + (name to (listOf(ms) + all[name].orEmpty()).take(MAX_HISTORY))),
            )
        }

        private companion object {
            const val HISTORY = "history"
            const val HOLDS = "holds"
            const val MAX_HISTORY = 20
        }
    }
