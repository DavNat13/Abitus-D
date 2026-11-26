package com.abitusd.app.feature.clock.data

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.clock.TimerProfileEntity
import com.abitusd.app.core.sync.SyncScheduler
import com.abitusd.app.feature.clock.domain.ClockMode
import com.abitusd.app.feature.clock.domain.ClockSpec
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Saved timer profiles, synced like any other table; removal is a tombstone. */
@Singleton
class ClockProfiles
    @Inject
    constructor(
        database: AbitusDatabase,
        private val scheduler: SyncScheduler,
    ) {
        private val dao = database.clockDao()
        val all = dao.profiles()

        fun spec(profile: TimerProfileEntity): ClockSpec? =
            runCatching {
                Json.decodeFromString<ClockSpec>(profile.spec)
            }.getOrNull()

        suspend fun save(
            name: String,
            mode: ClockMode,
            spec: ClockSpec,
        ) {
            val row =
                TimerProfileEntity(
                    UUID.randomUUID().toString(),
                    name.trim().take(MAX_NAME),
                    mode.key,
                    Json.encodeToString(spec),
                    System.currentTimeMillis(),
                    dirty = true,
                )
            dao.upsertProfiles(listOf(row))
            scheduler.requestSync()
        }

        suspend fun delete(id: String) {
            val row = dao.findProfile(id) ?: return
            val at = maxOf(System.currentTimeMillis(), row.updatedAt + 1)
            dao.upsertProfiles(listOf(row.copy(updatedAt = at, deletedAt = at, dirty = true)))
            scheduler.requestSync()
        }

        /** Imports a shared profile; anything that is not a valid profile is ignored. */
        suspend fun importJson(text: String): Boolean {
            val shared = runCatching { Json.decodeFromString<SharedProfile>(text) }.getOrNull() ?: return false
            val valid = shared.format == FORMAT && shared.name.isNotBlank() && shared.spec.rounds in 1..MAX_ROUNDS
            if (valid) save(shared.name, ClockMode.of(shared.mode), shared.spec)
            return valid
        }

        companion object {
            private const val MAX_NAME = 60
            private const val MAX_ROUNDS = 100
            private const val FORMAT = "abitusd-timer-profile-v1"

            fun exportJson(
                name: String,
                mode: ClockMode,
                spec: ClockSpec,
            ): String = Json.encodeToString(SharedProfile(FORMAT, name, mode.key, spec))
        }
    }

@kotlinx.serialization.Serializable
private data class SharedProfile(
    val format: String,
    val name: String,
    val mode: String,
    val spec: ClockSpec,
)
