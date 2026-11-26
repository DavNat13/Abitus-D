package com.abitusd.app.core.database.clock

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ClockDao {
    @Upsert suspend fun upsertFavorites(rows: List<ExerciseFavoriteEntity>)

    @Query("SELECT exerciseId FROM exercise_favorites WHERE deletedAt IS NULL")
    fun favoriteIds(): Flow<List<String>>

    @Query("SELECT * FROM exercise_favorites WHERE id = :id")
    suspend fun findFavorite(id: String): ExerciseFavoriteEntity?

    @Query("SELECT * FROM exercise_favorites WHERE dirty = 1")
    suspend fun dirtyFavorites(): List<ExerciseFavoriteEntity>

    @Query("UPDATE exercise_favorites SET dirty = 0 WHERE id = :id AND updatedAt = :at")
    suspend fun cleanFavorite(
        id: String,
        at: Long,
    )

    @Upsert suspend fun upsertProfiles(rows: List<TimerProfileEntity>)

    @Query("SELECT * FROM timer_profiles WHERE deletedAt IS NULL ORDER BY name")
    fun profiles(): Flow<List<TimerProfileEntity>>

    @Query("SELECT * FROM timer_profiles WHERE id = :id")
    suspend fun findProfile(id: String): TimerProfileEntity?

    @Query("SELECT * FROM timer_profiles WHERE dirty = 1")
    suspend fun dirtyProfiles(): List<TimerProfileEntity>

    @Query("UPDATE timer_profiles SET dirty = 0 WHERE id = :id AND updatedAt = :at")
    suspend fun cleanProfile(
        id: String,
        at: Long,
    )

    @Query("DELETE FROM exercise_favorites")
    suspend fun clearFavorites()

    @Query("DELETE FROM timer_profiles")
    suspend fun clearProfiles()

    @Query(
        "SELECT s.id, s.date, s.plannedTime AS time, COALESCE(r.name, '') AS title FROM scheduled_workouts s " +
            "LEFT JOIN routines r ON r.id = s.routineId WHERE s.deletedAt IS NULL AND s.plannedTime IS NOT NULL " +
            "AND s.status = 'planned' AND s.date >= :today",
    )
    suspend fun upcomingReminders(today: String): List<ReminderRow>
}

data class ReminderRow(
    val id: String,
    val date: String,
    val time: String,
    val title: String,
)
