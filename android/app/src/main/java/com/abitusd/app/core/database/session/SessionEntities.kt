package com.abitusd.app.core.database.session

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.abitusd.app.core.database.planning.SyncRow
import kotlinx.serialization.Serializable

/**
 * Workout logging rows (08b, 10f Fase 4). Entity and wire row at once, like the planning rows;
 * instants are epoch milliseconds here and ISO 8601 on the wire (PlanningCodec). Units: kg, m, s.
 */
@Serializable
@Entity(tableName = "workout_sessions", indices = [Index("startedAt"), Index("dirty")])
data class WorkoutSessionEntity(
    @PrimaryKey override val id: String,
    val routineId: String? = null,
    val scheduledWorkoutId: String? = null,
    val title: String,
    val startedAt: Long,
    val endedAt: Long? = null,
    val notes: String = "",
    val sessionRpe: Double? = null,
    val sleepQuality: Int? = null,
    val energy: Int? = null,
    val bodyweightKg: Double? = null,
    override val updatedAt: Long,
    override val deletedAt: Long? = null,
    override val dirty: Boolean = false,
    val serverSeq: Long = 0,
) : SyncRow

@Serializable
@Entity(tableName = "session_exercises", indices = [Index("sessionId"), Index("dirty")])
data class SessionExerciseEntity(
    @PrimaryKey override val id: String,
    val sessionId: String,
    val exerciseId: String,
    val position: Int,
    val groupId: String = "",
    val notes: String = "",
    val discomfort: String = "",
    override val updatedAt: Long,
    override val deletedAt: Long? = null,
    override val dirty: Boolean = false,
    val serverSeq: Long = 0,
) : SyncRow

@Serializable
@Entity(
    tableName = "sets",
    indices = [Index("sessionExerciseId"), Index(value = ["exerciseId", "performedAt"]), Index("dirty")],
)
data class SetEntity(
    @PrimaryKey override val id: String,
    val sessionExerciseId: String,
    val exerciseId: String,
    val performedAt: Long,
    val setIndex: Int,
    val setType: String = SetType.NORMAL,
    val side: String = "both",
    val reps: Int? = null,
    val loadKg: Double? = null,
    val addedLoadKg: Double? = null,
    val durationS: Int? = null,
    val distanceM: Double? = null,
    val rpe: Double? = null,
    val rir: Int? = null,
    val restS: Int? = null,
    val tempo: String = "",
    val completedAt: Long? = null,
    val notes: String = "",
    override val updatedAt: Long,
    override val deletedAt: Long? = null,
    override val dirty: Boolean = false,
    val serverSeq: Long = 0,
) : SyncRow

/** Set types accepted by the server (013_sessions.sql). */
object SetType {
    const val NORMAL = "normal"
    const val WARMUP = "warmup"
    const val DROP = "drop"
    const val AMRAP = "amrap"
    const val FAILURE = "failure"
    const val REST_PAUSE = "rest_pause"
    val ALL = listOf(NORMAL, WARMUP, DROP, AMRAP, FAILURE, REST_PAUSE)
}

/**
 * Local only: where the active session is, so it survives process death (09b).
 * The rest end is a wall-clock instant; the timer service converts it to its monotonic clock.
 */
@Entity(tableName = "active_session")
data class ActiveSessionEntity(
    @PrimaryKey val key: Int = 0,
    val sessionId: String,
    val exerciseIndex: Int = 0,
    val restEndsAt: Long? = null,
    val restTotalS: Int = 0,
)
