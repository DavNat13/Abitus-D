package com.abitusd.app.core.database.catalog

import androidx.room3.ColumnTypeConverter
import kotlinx.serialization.json.Json

/** Stores lists of short texts (execution steps, cues) as a JSON array. */
class StringListConverter {
    @ColumnTypeConverter
    fun fromList(value: List<String>): String = Json.encodeToString(value)

    @ColumnTypeConverter
    fun toList(value: String): List<String> = Json.decodeFromString(value)
}
