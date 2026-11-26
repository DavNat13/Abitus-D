package com.abitusd.app.feature.progress.data

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.progress.BodyMeasurementEntity
import com.abitusd.app.core.sync.SyncScheduler
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Body measurements, saved locally first and synced; deleting is a tombstone. */
@Singleton
class MeasurementRepository
    @Inject
    constructor(
        database: AbitusDatabase,
        private val scheduler: SyncScheduler,
    ) {
        private val dao = database.measurementDao()
        val all = dao.all()

        suspend fun latestWeight(): Double? = dao.latestWeight()

        suspend fun save(row: BodyMeasurementEntity) {
            val previous = dao.find(row.id)
            val at = maxOf(System.currentTimeMillis(), (previous?.updatedAt ?: 0) + 1)
            dao.upsert(
                listOf(row.copy(id = row.id.ifBlank { UUID.randomUUID().toString() }, updatedAt = at, dirty = true)),
            )
            scheduler.requestSync()
        }

        suspend fun delete(id: String) {
            val row = dao.find(id) ?: return
            val at = maxOf(System.currentTimeMillis(), row.updatedAt + 1)
            dao.upsert(listOf(row.copy(updatedAt = at, deletedAt = at, dirty = true)))
            scheduler.requestSync()
        }
    }
