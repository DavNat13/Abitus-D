package com.abitusd.app.core.sync

import com.abitusd.app.core.database.SyncStateDao
import com.abitusd.app.core.database.SyncStateEntity
import com.abitusd.app.core.network.ApiResult
import com.abitusd.app.core.network.DataApi
import com.abitusd.app.core.network.apiCall
import com.abitusd.app.core.network.dto.PushRequest
import com.abitusd.app.feature.library.data.CatalogStore
import com.abitusd.app.feature.library.data.OwnExerciseRepository
import javax.inject.Inject
import javax.inject.Singleton

/** Outcome of one sync run, used by the worker to decide whether to retry. */
enum class SyncOutcome { DONE, RETRY }

/**
 * One sync pass (09b, Motor de sincronización): catalog refresh, then push of pending local
 * changes in batches of 200, then pull from the stored cursor until there is nothing more.
 */
@Singleton
class SyncEngine
    @Inject
    constructor(
        private val api: DataApi,
        private val catalog: CatalogStore,
        private val ownExercises: OwnExerciseRepository,
        private val syncState: SyncStateDao,
    ) {
        suspend fun run(): SyncOutcome {
            val catalogResult = catalog.refresh()
            if (catalogResult.needsRetry()) return SyncOutcome.RETRY
            if (!push()) return SyncOutcome.RETRY
            return if (pull()) SyncOutcome.DONE else SyncOutcome.RETRY
        }

        private suspend fun push(): Boolean {
            for (batch in ownExercises.pendingChanges().chunked(BATCH)) {
                val request = PushRequest(batch.map { it.first })
                when (val result = apiCall { api.push(request) }) {
                    is ApiResult.Success -> {
                        val handled = result.value.accepted + result.value.rejected.map { it.id }
                        batch.filter { (change, _) -> changeId(change) in handled }
                            .forEach { (change, time) -> ownExercises.markSent(changeId(change), time) }
                    }
                    else -> return !result.needsRetry()
                }
            }
            return true
        }

        private suspend fun pull(): Boolean {
            var cursor = syncState.get(CURSOR_KEY)?.toLongOrNull() ?: 0L
            do {
                val result = apiCall { api.pull(cursor) }
                if (result !is ApiResult.Success) return !result.needsRetry()
                val page = result.value
                if (cursor < page.minValidCursor) cursor = 0L
                ownExercises.applyPulled(page.changes.filter { it.table == "exercises" })
                cursor = page.nextCursor
                syncState.put(SyncStateEntity(CURSOR_KEY, cursor.toString()))
            } while (page.hasMore)
            return true
        }

        private fun changeId(change: com.abitusd.app.core.network.dto.PushChangeDto): String =
            when (change) {
                is com.abitusd.app.core.network.dto.PushChangeDto.Upsert -> change.row.id
                is com.abitusd.app.core.network.dto.PushChangeDto.Delete -> change.row.id
            }

        private fun ApiResult<*>.needsRetry(): Boolean = this is ApiResult.Offline || this is ApiResult.Unexpected

        private companion object {
            const val BATCH = 200
            const val CURSOR_KEY = "pull_cursor"
        }
    }
