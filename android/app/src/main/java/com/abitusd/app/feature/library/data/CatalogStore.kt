package com.abitusd.app.feature.library.data

import androidx.room3.withWriteTransaction
import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.SyncStateEntity
import com.abitusd.app.core.network.ApiResult
import com.abitusd.app.core.network.DataApi
import com.abitusd.app.core.network.apiCall
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Downloads the catalog incrementally and applies it to Room in one transaction, so the
 * library works offline after the first download (09b, Catálogo).
 */
@Singleton
class CatalogStore
    @Inject
    constructor(
        private val database: AbitusDatabase,
        private val api: DataApi,
    ) {
        suspend fun refresh(): ApiResult<Int> {
            val since = database.syncStateDao().get(VERSION_KEY)?.toIntOrNull() ?: 0
            return when (val result = apiCall { api.catalog(since) }) {
                is ApiResult.Success -> {
                    apply(CatalogMapper.toRows(result.value, System.currentTimeMillis()), result.value.version)
                    ApiResult.Success(result.value.exercises.size)
                }

                is ApiResult.Problem -> {
                    result
                }

                ApiResult.Offline -> {
                    ApiResult.Offline
                }

                ApiResult.Unexpected -> {
                    ApiResult.Unexpected
                }
            }
        }

        private suspend fun apply(
            rows: CatalogRows,
            version: Int,
        ) {
            val dao = database.catalogWriteDao()
            database.withWriteTransaction {
                dao.upsertDisciplines(rows.disciplines)
                dao.upsertDisciplineTexts(rows.disciplineTexts)
                dao.upsertMuscleGroups(rows.muscles)
                dao.upsertMuscleGroupTexts(rows.muscleTexts)
                dao.upsertEquipment(rows.equipment)
                dao.upsertEquipmentTexts(rows.equipmentTexts)
                val ids = rows.exercises.map { it.id }
                ids.chunked(CHUNK).forEach { chunk ->
                    dao.deleteTexts(chunk)
                    dao.deleteMuscles(chunk)
                    dao.deleteEquipment(chunk)
                    dao.deleteRelations(chunk)
                }
                dao.upsertExercises(rows.exercises)
                dao.upsertExerciseTexts(rows.texts)
                dao.upsertExerciseMuscles(rows.exerciseMuscles)
                dao.upsertExerciseEquipment(rows.exerciseEquipment)
                dao.upsertExerciseRelations(rows.relations)
                database.syncStateDao().put(SyncStateEntity(VERSION_KEY, version.toString()))
            }
        }

        private companion object {
            const val VERSION_KEY = "catalog_version"
            const val CHUNK = 500
        }
    }
