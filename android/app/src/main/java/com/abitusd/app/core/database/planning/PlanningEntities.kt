package com.abitusd.app.core.database.planning

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Planning rows (08b). Each is a Room entity and the wire row of the sync protocol at once:
 * PlanningCodec drops the local fields (deletedAt, dirty, serverSeq) and converts updatedAt.
 * Times are epoch milliseconds; calendar dates are ISO strings (YYYY-MM-DD). Units: kg, m, s.
 */
@Serializable
@Entity(tableName = "routines", indices = [Index("dirty")])
data class RoutineEntity(
    @PrimaryKey val id: String,
    val name: String,
    val notes: String = "",
    val folder: String = "",
    val colorKey: String = "",
    val archived: Boolean = false,
    val updatedAt: Long,
    val deletedAt: Long? = null,
    val dirty: Boolean = false,
    val serverSeq: Long = 0,
)

@Serializable
@Entity(tableName = "routine_exercises", indices = [Index("routineId"), Index("dirty")])
data class RoutineExerciseEntity(
    @PrimaryKey val id: String,
    val routineId: String,
    val exerciseId: String,
    val position: Int,
    val groupId: String = "",
    val targetSets: Int? = null,
    val targetRepsMin: Int? = null,
    val targetRepsMax: Int? = null,
    val targetLoadKg: Double? = null,
    val targetRpe: Double? = null,
    val targetRir: Int? = null,
    val targetDurationS: Int? = null,
    val targetDistanceM: Double? = null,
    val restSeconds: Int? = null,
    val notes: String = "",
    val updatedAt: Long,
    val deletedAt: Long? = null,
    val dirty: Boolean = false,
    val serverSeq: Long = 0,
)

@Serializable
@Entity(tableName = "programs", indices = [Index("dirty")])
data class ProgramEntity(
    @PrimaryKey val id: String,
    val name: String,
    val startDate: String? = null,
    val weeksCount: Int,
    val isActive: Boolean = false,
    val notes: String = "",
    val updatedAt: Long,
    val deletedAt: Long? = null,
    val dirty: Boolean = false,
    val serverSeq: Long = 0,
)

@Serializable
@Entity(tableName = "program_days", indices = [Index("programId"), Index("dirty")])
data class ProgramDayEntity(
    @PrimaryKey val id: String,
    val programId: String,
    val weekIndex: Int,
    val dayIndex: Int,
    val routineId: String? = null,
    val isRest: Boolean = false,
    val updatedAt: Long,
    val deletedAt: Long? = null,
    val dirty: Boolean = false,
    val serverSeq: Long = 0,
)

@Serializable
@Entity(tableName = "scheduled_workouts", indices = [Index("date"), Index("dirty")])
data class ScheduledWorkoutEntity(
    @PrimaryKey val id: String,
    val date: String,
    val routineId: String? = null,
    val programDayId: String? = null,
    val status: String = "planned",
    val sessionId: String? = null,
    val rescheduledFrom: String? = null,
    val updatedAt: Long,
    val deletedAt: Long? = null,
    val dirty: Boolean = false,
    val serverSeq: Long = 0,
)
