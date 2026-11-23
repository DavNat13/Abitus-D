package com.abitusd.app.core.sync

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.session.SessionExerciseEntity
import com.abitusd.app.core.database.session.SetEntity
import com.abitusd.app.core.database.session.WorkoutSessionEntity

/** Workout logging tables in push order: sessions, their exercises, then the sets. */
fun sessionAdapters(database: AbitusDatabase): List<SyncTableAdapter> {
    val dao = database.sessionDao()
    return listOf(
        PlanningSyncAdapter(
            PlanningTableAccess(
                "workout_sessions",
                WorkoutSessionEntity.serializer(),
                dao::dirtySessions,
                dao::session,
                dao::upsertSessions,
                dao::cleanSession,
            ),
        ),
        PlanningSyncAdapter(
            PlanningTableAccess(
                "session_exercises",
                SessionExerciseEntity.serializer(),
                dao::dirtySessionExercises,
                dao::sessionExercise,
                dao::upsertSessionExercises,
                dao::cleanSessionExercise,
            ),
        ),
        PlanningSyncAdapter(
            PlanningTableAccess(
                "sets",
                SetEntity.serializer(),
                dao::dirtySets,
                dao::set,
                dao::upsertSets,
                dao::cleanSet,
            ),
        ),
    )
}
