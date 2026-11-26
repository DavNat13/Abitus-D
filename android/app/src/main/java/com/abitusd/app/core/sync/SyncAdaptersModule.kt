package com.abitusd.app.core.sync

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.planning.ProgramDayEntity
import com.abitusd.app.core.database.planning.ProgramEntity
import com.abitusd.app.core.database.planning.RoutineEntity
import com.abitusd.app.core.database.planning.RoutineExerciseEntity
import com.abitusd.app.core.database.planning.ScheduledWorkoutEntity
import com.abitusd.app.core.network.dto.PullChangeDto
import com.abitusd.app.feature.library.data.OwnExerciseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Own exercises keep their own repository (texts, muscles, equipment). */
class OwnExerciseSyncAdapter(
    private val repository: OwnExerciseRepository,
) : SyncTableAdapter {
    override val table = "exercises"

    override suspend fun pending() =
        repository.pendingChanges().map { (change, at) -> PendingChange(change, change.rowId(), at) }

    override suspend fun markSent(
        id: String,
        updatedAt: Long,
    ) = repository.markSent(id, updatedAt)

    override suspend fun apply(changes: List<PullChangeDto>) = repository.applyPulled(changes)

    private fun com.abitusd.app.core.network.dto.PushChangeDto.rowId(): String =
        (row.getValue("id") as kotlinx.serialization.json.JsonPrimitive).content
}

/** Every synced table in push order: exercises before the routines that reference them. */
@Module
@InstallIn(SingletonComponent::class)
object SyncAdaptersModule {
    @Provides
    @Singleton
    fun adapters(
        database: AbitusDatabase,
        ownExercises: OwnExerciseRepository,
    ): List<SyncTableAdapter> {
        val dao = database.planningDao()
        return listOf(
            OwnExerciseSyncAdapter(ownExercises),
            PlanningSyncAdapter(
                PlanningTableAccess(
                    "routines",
                    RoutineEntity.serializer(),
                    dao::dirtyRoutines,
                    dao::routine,
                    dao::upsertRoutines,
                    dao::cleanRoutine,
                ),
            ),
            PlanningSyncAdapter(
                PlanningTableAccess(
                    "routine_exercises",
                    RoutineExerciseEntity.serializer(),
                    dao::dirtyRoutineExercises,
                    dao::routineExercise,
                    dao::upsertRoutineExercises,
                    dao::cleanRoutineExercise,
                ),
            ),
            PlanningSyncAdapter(
                PlanningTableAccess(
                    "programs",
                    ProgramEntity.serializer(),
                    dao::dirtyPrograms,
                    dao::program,
                    dao::upsertPrograms,
                    dao::cleanProgram,
                ),
            ),
            PlanningSyncAdapter(
                PlanningTableAccess(
                    "program_days",
                    ProgramDayEntity.serializer(),
                    dao::dirtyProgramDays,
                    dao::programDay,
                    dao::upsertProgramDays,
                    dao::cleanProgramDay,
                ),
            ),
            PlanningSyncAdapter(
                PlanningTableAccess(
                    "scheduled_workouts",
                    ScheduledWorkoutEntity.serializer(),
                    dao::dirtyScheduled,
                    dao::scheduled,
                    dao::upsertScheduled,
                    dao::cleanScheduled,
                ),
            ),
        ) + sessionAdapters(database) + periodizationAdapters(database) + athleteAdapter(database) +
            clockAdapters(database)
    }
}
