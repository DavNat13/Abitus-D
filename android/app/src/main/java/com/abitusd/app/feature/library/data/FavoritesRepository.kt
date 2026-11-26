package com.abitusd.app.feature.library.data

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.clock.ExerciseFavoriteEntity
import com.abitusd.app.core.session.SessionManager
import com.abitusd.app.core.sync.SyncScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Favorites are tombstoned, never deleted, so a removal reaches every device. */
@Singleton
class FavoritesRepository
    @Inject
    constructor(
        database: AbitusDatabase,
        private val sessions: SessionManager,
        private val scheduler: SyncScheduler,
    ) {
        private val dao = database.clockDao()

        val ids: Flow<Set<String>> = dao.favoriteIds().map { it.toSet() }

        suspend fun toggle(exerciseId: String) {
            val user = requireNotNull(sessions.current()).userId
            val id = UUID.nameUUIDFromBytes("$user:$exerciseId".toByteArray()).toString()
            val previous = dao.findFavorite(id)
            val at = maxOf(System.currentTimeMillis(), (previous?.updatedAt ?: 0) + 1)
            val removed = previous != null && previous.deletedAt == null
            dao.upsertFavorites(
                listOf(ExerciseFavoriteEntity(id, exerciseId, at, deletedAt = at.takeIf { removed }, dirty = true)),
            )
            scheduler.requestSync()
        }

        companion object {
            /** Favorites first, keeping the original order inside each group. */
            fun <T> arrange(
                items: List<T>,
                favorites: Set<String>,
                onlyFavorites: Boolean,
                id: (T) -> String,
            ): List<T> {
                val (fav, rest) = items.partition { id(it) in favorites }
                return if (onlyFavorites) fav else fav + rest
            }
        }
    }
