package com.abitusd.app.feature.onboarding.data

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.athlete.AthleteProfileEntity
import com.abitusd.app.core.session.SessionManager
import com.abitusd.app.core.session.SessionState
import com.abitusd.app.core.sync.SyncScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AthleteRepository
    @Inject
    constructor(
        database: AbitusDatabase,
        private val sessions: SessionManager,
        private val scheduler: SyncScheduler,
    ) {
        private val dao = database.athleteDao()

        @OptIn(ExperimentalCoroutinesApi::class)
        val profile =
            sessions.state.flatMapLatest { state ->
                if (state is SessionState.SignedIn) dao.observe(state.userId) else flowOf(null)
            }

        suspend fun load(): AthleteProfileEntity {
            val id = requireNotNull(sessions.current()).userId
            return dao.find(id)?.takeIf { it.deletedAt == null } ?: AthleteProfileEntity(id, updatedAt = 0)
        }

        suspend fun save(profile: AthleteProfileEntity) {
            require(profile.id == requireNotNull(sessions.current()).userId)
            val previous = dao.find(profile.id)
            val at = maxOf(System.currentTimeMillis(), (previous?.updatedAt ?: 0) + 1)
            val safe = if (profile.healthConsent) profile else profile.copy(healthLimited = null, injuries = "")
            dao.upsert(listOf(safe.copy(updatedAt = at, dirty = true, deletedAt = null)))
            scheduler.requestSync()
        }
    }
