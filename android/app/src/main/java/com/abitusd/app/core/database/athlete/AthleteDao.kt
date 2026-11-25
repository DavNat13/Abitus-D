package com.abitusd.app.core.database.athlete

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AthleteDao {
    @Upsert suspend fun upsert(rows: List<AthleteProfileEntity>)

    @Query("SELECT * FROM athlete_profiles WHERE id = :id AND deletedAt IS NULL")
    fun observe(id: String): Flow<AthleteProfileEntity?>

    @Query("SELECT * FROM athlete_profiles WHERE id = :id")
    suspend fun find(id: String): AthleteProfileEntity?

    @Query("SELECT * FROM athlete_profiles WHERE dirty = 1")
    suspend fun dirty(): List<AthleteProfileEntity>

    @Query("UPDATE athlete_profiles SET dirty = 0 WHERE id = :id AND updatedAt = :at")
    suspend fun clean(
        id: String,
        at: Long,
    )

    @Query("DELETE FROM athlete_profiles")
    suspend fun clear()
}
