package com.abitusd.app.core.database

import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.RoomDatabase
import androidx.room3.Upsert

/**
 * Local source of truth (09b-FRONTEND-OFFLINE-SYNC.md). Phase 1 only creates the base and the
 * sync state table; user tables arrive with their phases through versioned migrations.
 */
@Database(entities = [SyncStateEntity::class], version = 1, exportSchema = true)
abstract class AbitusDatabase : RoomDatabase() {
    abstract fun syncStateDao(): SyncStateDao

    companion object {
        const val NAME = "abitusd.db"
    }
}

/** Key value pairs of the sync engine: pull cursor, catalog version. */
@Entity(tableName = "sync_state")
data class SyncStateEntity(
    @PrimaryKey val key: String,
    val value: String,
)

@Dao
interface SyncStateDao {
    @Query("SELECT value FROM sync_state WHERE `key` = :key")
    suspend fun get(key: String): String?

    @Upsert
    suspend fun put(entity: SyncStateEntity)

    @Query("DELETE FROM sync_state")
    suspend fun clear()
}
