package com.abitusd.app.core.sync

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.progress.BodyMeasurementEntity

fun progressAdapters(database: AbitusDatabase): List<SyncTableAdapter> {
    val dao = database.measurementDao()
    return listOf(
        PlanningSyncAdapter(
            PlanningTableAccess(
                "body_measurements",
                BodyMeasurementEntity.serializer(),
                dao::dirty,
                dao::find,
                dao::upsert,
                dao::clean,
            ),
        ),
    )
}
