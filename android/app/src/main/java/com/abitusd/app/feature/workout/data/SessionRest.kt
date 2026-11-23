package com.abitusd.app.feature.workout.data

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.timer.RestScheduler
import javax.inject.Inject

/** Rest of the active session: persisted end time plus the exact alarm (10f, 09c). */
class SessionRest
    @Inject
    constructor(
        private val database: AbitusDatabase,
        private val scheduler: RestScheduler,
    ) {
        private val dao get() = database.sessionDao()

        suspend fun start(
            seconds: Int,
            label: String,
        ) {
            if (seconds <= 0) return
            val endsAt = System.currentTimeMillis() + seconds * MS
            dao.active()?.let { dao.setActive(it.copy(restEndsAt = endsAt, restTotalS = seconds)) }
            scheduler.start(endsAt, label)
        }

        suspend fun skip() {
            scheduler.cancel()
            dao.active()?.let { dao.setActive(it.copy(restEndsAt = null, restTotalS = 0)) }
        }

        suspend fun addFifteen() {
            val active = dao.active() ?: return
            val endsAt = (active.restEndsAt ?: return) + EXTRA_S * MS
            dao.setActive(active.copy(restEndsAt = endsAt, restTotalS = active.restTotalS + EXTRA_S))
            scheduler.start(endsAt, "")
        }

        private companion object {
            const val MS = 1000L
            const val EXTRA_S = 15
        }
    }
