package com.abitusd.app.core.database.planning

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

/** Planning reads and writes. Every query skips soft-deleted rows unless it is for sync. */
@Dao
interface PlanningDao {
    @Upsert suspend fun upsertRoutines(rows: List<RoutineEntity>)

    @Upsert suspend fun upsertRoutineExercises(rows: List<RoutineExerciseEntity>)

    @Upsert suspend fun upsertPrograms(rows: List<ProgramEntity>)

    @Upsert suspend fun upsertProgramDays(rows: List<ProgramDayEntity>)

    @Upsert suspend fun upsertScheduled(rows: List<ScheduledWorkoutEntity>)

    @Query("SELECT * FROM routines WHERE dirty = 1")
    suspend fun dirtyRoutines(): List<RoutineEntity>

    @Query("SELECT * FROM routine_exercises WHERE dirty = 1")
    suspend fun dirtyRoutineExercises(): List<RoutineExerciseEntity>

    @Query("SELECT * FROM programs WHERE dirty = 1")
    suspend fun dirtyPrograms(): List<ProgramEntity>

    @Query("SELECT * FROM program_days WHERE dirty = 1")
    suspend fun dirtyProgramDays(): List<ProgramDayEntity>

    @Query("SELECT * FROM scheduled_workouts WHERE dirty = 1")
    suspend fun dirtyScheduled(): List<ScheduledWorkoutEntity>

    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun routine(id: String): RoutineEntity?

    @Query("SELECT * FROM routine_exercises WHERE id = :id")
    suspend fun routineExercise(id: String): RoutineExerciseEntity?

    @Query("SELECT * FROM programs WHERE id = :id")
    suspend fun program(id: String): ProgramEntity?

    @Query("SELECT * FROM program_days WHERE id = :id")
    suspend fun programDay(id: String): ProgramDayEntity?

    @Query("SELECT * FROM scheduled_workouts WHERE id = :id")
    suspend fun scheduled(id: String): ScheduledWorkoutEntity?

    @Query("UPDATE routines SET dirty = 0 WHERE id = :id AND updatedAt = :at")
    suspend fun cleanRoutine(
        id: String,
        at: Long,
    )

    @Query("UPDATE routine_exercises SET dirty = 0 WHERE id = :id AND updatedAt = :at")
    suspend fun cleanRoutineExercise(
        id: String,
        at: Long,
    )

    @Query("UPDATE programs SET dirty = 0 WHERE id = :id AND updatedAt = :at")
    suspend fun cleanProgram(
        id: String,
        at: Long,
    )

    @Query("UPDATE program_days SET dirty = 0 WHERE id = :id AND updatedAt = :at")
    suspend fun cleanProgramDay(
        id: String,
        at: Long,
    )

    @Query("UPDATE scheduled_workouts SET dirty = 0 WHERE id = :id AND updatedAt = :at")
    suspend fun cleanScheduled(
        id: String,
        at: Long,
    )

    @Query(
        "SELECT * FROM routines WHERE deletedAt IS NULL ORDER BY archived, folder COLLATE NOCASE, name COLLATE NOCASE",
    )
    fun routines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId AND deletedAt IS NULL ORDER BY position")
    fun routineExercises(routineId: String): Flow<List<RoutineExerciseEntity>>

    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId AND deletedAt IS NULL ORDER BY position")
    suspend fun routineExercisesOnce(routineId: String): List<RoutineExerciseEntity>

    @Query("SELECT * FROM programs WHERE deletedAt IS NULL ORDER BY isActive DESC, name COLLATE NOCASE")
    fun programs(): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM program_days WHERE programId = :programId AND deletedAt IS NULL ORDER BY weekIndex, dayIndex")
    fun programDays(programId: String): Flow<List<ProgramDayEntity>>

    @Query("SELECT * FROM program_days WHERE programId = :programId AND deletedAt IS NULL")
    suspend fun programDaysOnce(programId: String): List<ProgramDayEntity>

    @Query("SELECT * FROM scheduled_workouts WHERE deletedAt IS NULL AND date BETWEEN :from AND :to ORDER BY date")
    fun scheduledBetween(
        from: String,
        to: String,
    ): Flow<List<ScheduledWorkoutEntity>>

    @Query("SELECT * FROM scheduled_workouts WHERE deletedAt IS NULL AND date BETWEEN :from AND :to")
    suspend fun scheduledBetweenOnce(
        from: String,
        to: String,
    ): List<ScheduledWorkoutEntity>

    @Query("DELETE FROM routines")
    suspend fun clearRoutines()

    @Query("DELETE FROM routine_exercises")
    suspend fun clearRoutineExercises()

    @Query("DELETE FROM programs")
    suspend fun clearPrograms()

    @Query("DELETE FROM program_days")
    suspend fun clearProgramDays()

    @Query("DELETE FROM scheduled_workouts")
    suspend fun clearScheduled()

    /** Removes every planning row of the signed-out user from this device. */
    suspend fun clearAll() {
        clearRoutines()
        clearRoutineExercises()
        clearPrograms()
        clearProgramDays()
        clearScheduled()
    }
}
