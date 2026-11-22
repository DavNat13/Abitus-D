package com.abitusd.app.core.sync

import com.abitusd.app.core.database.SyncStateDao
import com.abitusd.app.core.database.SyncStateEntity
import com.abitusd.app.core.network.ApiResult
import com.abitusd.app.core.network.DataApi
import com.abitusd.app.core.network.apiCall
import com.abitusd.app.core.network.dto.PushRequest
import com.abitusd.app.feature.library.data.CatalogStore
import javax.inject.Inject
import javax.inject.Singleton

/** Outcome of one sync run, used by the worker to decide whether to retry. */
enum class SyncOutcome { DONE, RETRY }

/**
 * One sync pass (09b, Motor de sincronización): catalog refresh, then push of pending local
 * changes of every table in batches of 200, then pull from the stored cursor until done.
 */
@Singleton
class SyncEngine
    @Inject
    constructor(
        private val api: DataApi,
        private val catalog: CatalogStore,
        private val adapters: List<@JvmSuppressWildcards SyncTableAdapter>,
        private val syncState: SyncStateDao,
    ) {
        suspend fun run(): SyncOutcome {
            // Each step runs only if the previous one did not ask for a retry.
            val completed = !catalog.refresh().needsRetry() && push() && pull()
            return if (completed) SyncOutcome.DONE else SyncOutcome.RETRY
        }

        private suspend fun push(): Boolean {
            val pending = adapters.flatMap { adapter -> adapter.pending().map { adapter to it } }
            for (batch in pending.chunked(BATCH)) {
                val result = apiCall { api.push(PushRequest(batch.map { it.second.change })) }
                if (result !is ApiResult.Success) return !result.needsRetry()
                val handled = (result.value.accepted + result.value.rejected.map { it.id }).toSet()
                batch
                    .filter { (_, p) -> p.id in handled }
                    .forEach { (adapter, p) -> adapter.markSent(p.id, p.updatedAt) }
            }
            return true
        }

        private suspend fun pull(): Boolean {
            var cursor = syncState.get(CURSOR_KEY)?.toLongOrNull() ?: 0L
            do {
                val result = apiCall { api.pull(cursor) }
                if (result !is ApiResult.Success) return !result.needsRetry()
                val page = result.value
                val byTable = page.changes.groupBy { it.table }
                adapters.forEach { adapter -> byTable[adapter.table]?.let { adapter.apply(it) } }
                cursor = page.nextCursor
                syncState.put(SyncStateEntity(CURSOR_KEY, cursor.toString()))
            } while (page.hasMore)
            return true
        }

        private fun ApiResult<*>.needsRetry(): Boolean = this is ApiResult.Offline || this is ApiResult.Unexpected

        private companion object {
            const val BATCH = 200
            const val CURSOR_KEY = "pull_cursor"
        }
    }
