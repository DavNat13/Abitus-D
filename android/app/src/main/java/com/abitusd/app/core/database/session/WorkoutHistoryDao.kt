package com.abitusd.app.core.database.session

import androidx.room3.Dao
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

/** Completed sets of one session, for the history list. */
data class SessionSetCount(
    val sessionId: String,
    val sets: Int,
)

/** History reads: previous bests for records and per-session counts. */
@Dao
interface WorkoutHistoryDao {
    /** Completed sets of these exercises in every other session (for personal records). */
    @Query(
        """
        SELECT s.* FROM sets s JOIN session_exercises e ON e.id = s.sessionExerciseId
        WHERE s.exerciseId IN (:exerciseIds) AND e.sessionId != :sessionId
          AND s.deletedAt IS NULL AND s.completedAt IS NOT NULL
        """,
    )
    suspend fun completedSetsOf(
        exerciseIds: List<String>,
        sessionId: String,
    ): List<SetEntity>

    @Query(
        """
        SELECT e.sessionId AS sessionId, COUNT(s.id) AS sets FROM session_exercises e
        JOIN sets s ON s.sessionExerciseId = e.id
        WHERE s.deletedAt IS NULL AND s.completedAt IS NOT NULL AND e.deletedAt IS NULL
        GROUP BY e.sessionId
        """,
    )
    fun setCounts(): Flow<List<SessionSetCount>>
}
