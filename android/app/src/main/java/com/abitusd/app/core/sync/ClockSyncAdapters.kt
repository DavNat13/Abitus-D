package com.abitusd.app.core.sync

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.clock.ExerciseFavoriteEntity
import com.abitusd.app.core.database.clock.TimerProfileEntity

fun clockAdapters(database: AbitusDatabase): List<SyncTableAdapter> {
    val dao = database.clockDao()
    return listOf(
        PlanningSyncAdapter(
            PlanningTableAccess(
                "exercise_favorites",
                ExerciseFavoriteEntity.serializer(),
                dao::dirtyFavorites,
                dao::findFavorite,
                dao::upsertFavorites,
                dao::cleanFavorite,
            ),
        ),
        PlanningSyncAdapter(
            PlanningTableAccess(
                "timer_profiles",
                TimerProfileEntity.serializer(),
                dao::dirtyProfiles,
                dao::findProfile,
                dao::upsertProfiles,
                dao::cleanProfile,
            ),
        ),
    )
}
