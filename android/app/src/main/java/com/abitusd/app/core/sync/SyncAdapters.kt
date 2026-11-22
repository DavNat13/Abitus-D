package com.abitusd.app.core.sync

import com.abitusd.app.core.database.planning.SyncRow
import com.abitusd.app.core.network.dto.PullChangeDto
import com.abitusd.app.core.network.dto.PushChangeDto
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.jsonPrimitive

/** Pending local change ready to push, with the local edit time used to clear it. */
data class PendingChange(
    val change: PushChangeDto,
    val id: String,
    val updatedAt: Long,
)

/** One synced table as seen by the sync engine. */
interface SyncTableAdapter {
    val table: String

    suspend fun pending(): List<PendingChange>

    suspend fun markSent(
        id: String,
        updatedAt: Long,
    )

    suspend fun apply(changes: List<PullChangeDto>)
}

/** Local access to one planning table; built in SyncAdaptersModule from PlanningDao. */
class PlanningTableAccess<T : SyncRow>(
    val table: String,
    val serializer: KSerializer<T>,
    val dirty: suspend () -> List<T>,
    val find: suspend (String) -> T?,
    val upsert: suspend (List<T>) -> Unit,
    val clean: suspend (String, Long) -> Unit,
)

/** Generic adapter for flat planning rows: last write wins, pending local edits are kept. */
class PlanningSyncAdapter<T : SyncRow>(
    private val access: PlanningTableAccess<T>,
) : SyncTableAdapter {
    override val table: String = access.table

    override suspend fun pending(): List<PendingChange> =
        access.dirty().map { row ->
            val wire = PlanningCodec.toWire(access.serializer, row)
            val op = if (row.deletedAt != null) "delete" else "upsert"
            val body = if (op == "delete") pickDeleteFields(wire) else wire
            PendingChange(PushChangeDto(table, op, body), row.id, row.updatedAt)
        }

    override suspend fun markSent(
        id: String,
        updatedAt: Long,
    ) = access.clean(id, updatedAt)

    override suspend fun apply(changes: List<PullChangeDto>) {
        val rows =
            changes.mapNotNull { change ->
                val id =
                    change.row
                        .getValue("id")
                        .jsonPrimitive.content
                val local = access.find(id)
                val remoteTime = PlanningCodec.updatedAtOf(change.row)
                val keepLocal = local != null && local.dirty && local.updatedAt > remoteTime
                if (keepLocal) {
                    null
                } else {
                    PlanningCodec.fromWire(
                        access.serializer,
                        change.row,
                        change.serverSeq,
                        change.op == "delete",
                    )
                }
            }
        if (rows.isNotEmpty()) access.upsert(rows)
    }

    private fun pickDeleteFields(wire: kotlinx.serialization.json.JsonObject) =
        kotlinx.serialization.json.JsonObject(wire.filterKeys { it == "id" || it == "updatedAt" })
}
