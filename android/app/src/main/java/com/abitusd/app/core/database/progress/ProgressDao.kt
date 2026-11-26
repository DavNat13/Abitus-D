package com.abitusd.app.core.database.progress

import androidx.room3.Dao
import androidx.room3.Query
import com.abitusd.app.feature.progress.domain.WorkSet
import kotlinx.coroutines.flow.Flow

/** An exercise the athlete has trained, with its catalog metric type. */
data class TrainedExercise(
    val exerciseId: String,
    val metricType: String,
    val sets: Int,
    val lastAt: Long,
)

/** Totals of the sessions that started in a time range. */
data class RangeTotals(
    val sessions: Int,
    val minutes: Long,
    val volumeKg: Double,
    val sets: Int,
)

/** Read-only queries of Progress (09d). Warm-ups and deleted rows never count. */
@Dao
interface ProgressDao {
    @Query(
        """
        SELECT s.exerciseId AS exerciseId, COALESCE(x.metricType, 'weight_reps') AS metricType,
            COUNT(s.id) AS sets, MAX(s.performedAt) AS lastAt
        FROM sets s JOIN session_exercises e ON e.id = s.sessionExerciseId
        LEFT JOIN exercises x ON x.id = s.exerciseId
        WHERE s.deletedAt IS NULL AND s.completedAt IS NOT NULL AND s.setType != 'warmup' AND e.deletedAt IS NULL
        GROUP BY s.exerciseId ORDER BY lastAt DESC
        """,
    )
    fun trainedExercises(): Flow<List<TrainedExercise>>

    @Query(
        """
        SELECT e.sessionId AS sessionId, s.performedAt AS at, s.reps AS reps, s.loadKg AS loadKg,
            s.addedLoadKg AS addedKg, s.durationS AS durationS
        FROM sets s JOIN session_exercises e ON e.id = s.sessionExerciseId
        JOIN workout_sessions w ON w.id = e.sessionId
        WHERE s.exerciseId = :exerciseId AND s.deletedAt IS NULL AND s.completedAt IS NOT NULL
            AND s.setType != 'warmup' AND e.deletedAt IS NULL AND w.deletedAt IS NULL
        ORDER BY s.performedAt
        """,
    )
    suspend fun workSets(exerciseId: String): List<WorkSet>

    @Query(
        """
        SELECT COUNT(DISTINCT w.id) AS sessions,
            (SELECT COALESCE(SUM((m.endedAt - m.startedAt) / 60000), 0) FROM workout_sessions m
                WHERE m.deletedAt IS NULL AND m.endedAt IS NOT NULL AND m.startedAt >= :from AND m.startedAt < :to) AS minutes,
            COALESCE(SUM(CASE WHEN s.setType != 'warmup' THEN COALESCE(s.loadKg, 0) * COALESCE(s.reps, 0) END), 0) AS volumeKg,
            COUNT(s.id) AS sets
        FROM workout_sessions w
        LEFT JOIN session_exercises e ON e.sessionId = w.id AND e.deletedAt IS NULL
        LEFT JOIN sets s ON s.sessionExerciseId = e.id AND s.deletedAt IS NULL AND s.completedAt IS NOT NULL
        WHERE w.deletedAt IS NULL AND w.startedAt >= :from AND w.startedAt < :to
        """,
    )
    fun totals(
        from: Long,
        to: Long,
    ): Flow<RangeTotals>
}
