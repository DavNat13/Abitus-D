package com.abitusd.app.core.database.session

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

/** Workout logging reads and writes; soft-deleted rows are hidden from the screens. */
@Dao
interface SessionDao {
    @Upsert suspend fun upsertSessions(rows: List<WorkoutSessionEntity>)

    @Upsert suspend fun upsertSessionExercises(rows: List<SessionExerciseEntity>)

    @Upsert suspend fun upsertSets(rows: List<SetEntity>)

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    suspend fun session(id: String): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    fun sessionFlow(id: String): Flow<WorkoutSessionEntity?>

    @Query("SELECT * FROM workout_sessions WHERE deletedAt IS NULL AND endedAt IS NOT NULL ORDER BY startedAt DESC")
    fun finishedSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM session_exercises WHERE id = :id")
    suspend fun sessionExercise(id: String): SessionExerciseEntity?

    @Query("SELECT * FROM session_exercises WHERE sessionId = :sessionId AND deletedAt IS NULL ORDER BY position")
    fun sessionExercises(sessionId: String): Flow<List<SessionExerciseEntity>>

    @Query("SELECT * FROM session_exercises WHERE sessionId = :sessionId AND deletedAt IS NULL ORDER BY position")
    suspend fun sessionExercisesOnce(sessionId: String): List<SessionExerciseEntity>

    @Query("SELECT * FROM sets WHERE id = :id")
    suspend fun set(id: String): SetEntity?

    @Query(
        """
        SELECT s.* FROM sets s JOIN session_exercises e ON e.id = s.sessionExerciseId
        WHERE e.sessionId = :sessionId AND s.deletedAt IS NULL ORDER BY e.position, s.setIndex
        """,
    )
    fun setsOfSession(sessionId: String): Flow<List<SetEntity>>

    @Query(
        """
        SELECT s.* FROM sets s JOIN session_exercises e ON e.id = s.sessionExerciseId
        WHERE e.sessionId = :sessionId AND s.deletedAt IS NULL ORDER BY e.position, s.setIndex
        """,
    )
    suspend fun setsOfSessionOnce(sessionId: String): List<SetEntity>

    /** Completed sets of the most recent other session with this exercise ("last time"). */
    @Query(
        """
        SELECT * FROM sets WHERE exerciseId = :exerciseId AND deletedAt IS NULL AND completedAt IS NOT NULL
          AND sessionExerciseId IN (SELECT id FROM session_exercises WHERE sessionId != :sessionId)
          AND performedAt = (SELECT MAX(performedAt) FROM sets WHERE exerciseId = :exerciseId
            AND deletedAt IS NULL AND completedAt IS NOT NULL
            AND sessionExerciseId IN (SELECT id FROM session_exercises WHERE sessionId != :sessionId))
        ORDER BY setIndex
        """,
    )
    suspend fun lastTime(
        exerciseId: String,
        sessionId: String,
    ): List<SetEntity>

    @Query("SELECT * FROM workout_sessions WHERE dirty = 1")
    suspend fun dirtySessions(): List<WorkoutSessionEntity>

    @Query("SELECT * FROM session_exercises WHERE dirty = 1")
    suspend fun dirtySessionExercises(): List<SessionExerciseEntity>

    @Query("SELECT * FROM sets WHERE dirty = 1")
    suspend fun dirtySets(): List<SetEntity>

    @Query("UPDATE workout_sessions SET dirty = 0 WHERE id = :id AND updatedAt = :updatedAt")
    suspend fun cleanSession(
        id: String,
        updatedAt: Long,
    )

    @Query("UPDATE session_exercises SET dirty = 0 WHERE id = :id AND updatedAt = :updatedAt")
    suspend fun cleanSessionExercise(
        id: String,
        updatedAt: Long,
    )

    @Query("UPDATE sets SET dirty = 0 WHERE id = :id AND updatedAt = :updatedAt")
    suspend fun cleanSet(
        id: String,
        updatedAt: Long,
    )

    @Query("SELECT * FROM active_session WHERE `key` = 0")
    fun activeFlow(): Flow<ActiveSessionEntity?>

    @Query("SELECT * FROM active_session WHERE `key` = 0")
    suspend fun active(): ActiveSessionEntity?

    @Upsert suspend fun setActive(row: ActiveSessionEntity)

    @Query("DELETE FROM active_session")
    suspend fun clearActive()

    @Query("DELETE FROM workout_sessions")
    suspend fun clearSessions()

    @Query("DELETE FROM session_exercises")
    suspend fun clearSessionExercises()

    @Query("DELETE FROM sets")
    suspend fun clearSets()
}
