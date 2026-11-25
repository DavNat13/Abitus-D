package com.abitusd.app.core.sync

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.athlete.AthleteProfileEntity

fun athleteAdapter(database: AbitusDatabase): SyncTableAdapter {
    val dao = database.athleteDao()
    return PlanningSyncAdapter(
        PlanningTableAccess(
            "athlete_profiles",
            AthleteProfileEntity.serializer(),
            dao::dirty,
            dao::find,
            dao::upsert,
            dao::clean,
        ),
    )
}
