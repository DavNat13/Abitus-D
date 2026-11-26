package com.abitusd.app.core.database.progress

import androidx.room3.Dao
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Upsert
import com.abitusd.app.core.database.planning.SyncRow
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/** One measuring day; SI units (kg, m). The date is the local ISO day. */
@Serializable
@Entity(tableName = "body_measurements", indices = [Index("measuredOn"), Index("dirty")])
data class BodyMeasurementEntity(
    @PrimaryKey override val id: String,
    val measuredOn: String,
    val weightKg: Double? = null,
    val waistM: Double? = null,
    val chestM: Double? = null,
    val hipM: Double? = null,
    val armM: Double? = null,
    val thighM: Double? = null,
    val note: String = "",
    override val updatedAt: Long,
    override val deletedAt: Long? = null,
    override val dirty: Boolean = false,
) : SyncRow

@Dao
interface MeasurementDao {
    @Upsert suspend fun upsert(rows: List<BodyMeasurementEntity>)

    @Query("SELECT * FROM body_measurements WHERE deletedAt IS NULL ORDER BY measuredOn")
    fun all(): Flow<List<BodyMeasurementEntity>>

    @Query(
        "SELECT weightKg FROM body_measurements WHERE deletedAt IS NULL AND weightKg IS NOT NULL " +
            "ORDER BY measuredOn DESC LIMIT 1",
    )
    suspend fun latestWeight(): Double?

    @Query("SELECT * FROM body_measurements WHERE id = :id")
    suspend fun find(id: String): BodyMeasurementEntity?

    @Query("SELECT * FROM body_measurements WHERE dirty = 1")
    suspend fun dirty(): List<BodyMeasurementEntity>

    @Query("UPDATE body_measurements SET dirty = 0 WHERE id = :id AND updatedAt = :at")
    suspend fun clean(
        id: String,
        at: Long,
    )

    @Query("DELETE FROM body_measurements")
    suspend fun clear()
}
