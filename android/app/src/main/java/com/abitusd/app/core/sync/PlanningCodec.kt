package com.abitusd.app.core.sync

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant

/**
 * Converts planning entities to and from wire rows: local fields are removed on the way out,
 * updatedAt goes from epoch milliseconds to ISO 8601 and back. Pure; tested on the JVM.
 */
object PlanningCodec {
    private val LOCAL_FIELDS = setOf("deletedAt", "dirty", "serverSeq")

    /** Instants stored as epoch milliseconds in Room and sent as ISO 8601. */
    private val INSTANT_FIELDS = setOf("updatedAt", "startedAt", "endedAt", "performedAt", "completedAt")
    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = true
        }

    fun <T> toWire(
        serializer: KSerializer<T>,
        entity: T,
    ): JsonObject {
        val obj = json.encodeToJsonElement(serializer, entity).jsonObject
        return JsonObject(
            obj.filterKeys { it !in LOCAL_FIELDS }.mapValues { (key, value) ->
                if (key in INSTANT_FIELDS && value is JsonPrimitive && value.content != "null") {
                    JsonPrimitive(Instant.ofEpochMilli(value.content.toLong()).toString())
                } else {
                    value
                }
            },
        )
    }

    /** Wire row to entity with the given server sequence; the row is clean (not dirty). */
    fun <T> fromWire(
        serializer: KSerializer<T>,
        row: JsonObject,
        serverSeq: Long,
        deleted: Boolean,
    ): T {
        val updatedAt = Instant.parse(row.getValue("updatedAt").jsonPrimitive.content).toEpochMilli()
        val converted =
            row.mapValues { (key, value) ->
                if (key in INSTANT_FIELDS && value is JsonPrimitive && value.isString) {
                    JsonPrimitive(Instant.parse(value.content).toEpochMilli())
                } else {
                    value
                }
            }
        val local =
            converted +
                mapOf(
                    "updatedAt" to JsonPrimitive(updatedAt),
                    "serverSeq" to JsonPrimitive(serverSeq),
                    "dirty" to JsonPrimitive(false),
                    "deletedAt" to (if (deleted) JsonPrimitive(updatedAt) else JsonPrimitive(null as Long?)),
                )
        return json.decodeFromJsonElement(serializer, JsonObject(local))
    }

    fun updatedAtOf(row: JsonObject): Long =
        Instant.parse(row.getValue("updatedAt").jsonPrimitive.content).toEpochMilli()
}
