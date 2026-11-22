package com.abitusd.app.feature.library.data

import androidx.room3.withWriteTransaction
import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.network.dto.DeletedRowDto
import com.abitusd.app.core.network.dto.PullChangeDto
import com.abitusd.app.core.network.dto.PushChangeDto
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Own exercises: written to Room first and marked dirty; the sync engine sends them later
 * (golden rule 12). Pulled changes never overwrite a newer local edit that is still pending.
 */
@Singleton
class OwnExerciseRepository
    @Inject
    constructor(
        private val database: AbitusDatabase,
    ) {
        private val dao get() = database.catalogWriteDao()

        suspend fun save(input: OwnExerciseInput) {
            write(OwnExerciseMapper.rows(input, System.currentTimeMillis(), dirty = true))
        }

        suspend fun delete(id: String) {
            val current = dao.exercise(id) ?: return
            val now = System.currentTimeMillis()
            dao.upsertExercises(listOf(current.copy(deletedAt = now, updatedAt = now, dirty = true)))
        }

        suspend fun load(id: String): OwnExerciseRows? {
            val exercise = dao.exercise(id)?.takeIf { it.isOwn }
            val text = exercise?.let { dao.texts(id).firstOrNull() }
            return if (exercise != null && text != null) {
                OwnExerciseRows(exercise, text, dao.muscles(id), dao.equipment(id))
            } else {
                null
            }
        }

        /** Pending local changes as push operations, with the local time of each edit. */
        suspend fun pendingChanges(): List<Pair<PushChangeDto, Long>> =
            dao.dirtyOwnExercises().mapNotNull { exercise ->
                if (exercise.deletedAt != null) {
                    val row = DeletedRowDto(exercise.id, Instant.ofEpochMilli(exercise.updatedAt).toString())
                    PushChangeDto.Delete(row = row) to exercise.updatedAt
                } else {
                    load(exercise.id)?.let {
                        PushChangeDto.Upsert(row = OwnExerciseMapper.toDto(it)) to
                            exercise.updatedAt
                    }
                }
            }

        /** Clears the pending mark only if the row was not edited again meanwhile. */
        suspend fun markSent(
            id: String,
            updatedAt: Long,
        ) = dao.markClean(id, updatedAt)

        suspend fun applyPulled(changes: List<PullChangeDto>) {
            database.withWriteTransaction {
                changes.forEach { applyOne(it) }
            }
        }

        private suspend fun applyOne(change: PullChangeDto) {
            val remoteTime = Instant.parse(change.row.updatedAt).toEpochMilli()
            val local = dao.exercise(change.row.id)
            val localIsNewer = local != null && local.dirty && local.updatedAt > remoteTime
            when {
                localIsNewer -> {
                    Unit
                }

                change.op == "delete" -> {
                    local?.let {
                        dao.upsertExercises(
                            listOf(it.copy(deletedAt = remoteTime, dirty = false, serverSeq = change.serverSeq)),
                        )
                    }
                }

                else -> {
                    val input = OwnExerciseMapper.fromDto(change.row)
                    write(OwnExerciseMapper.rows(input, remoteTime, dirty = false, serverSeq = change.serverSeq))
                }
            }
        }

        suspend fun clear() = dao.clearOwnExercises()

        private suspend fun write(rows: OwnExerciseRows) {
            database.withWriteTransaction {
                val ids = listOf(rows.exercise.id)
                dao.deleteTexts(ids)
                dao.deleteMuscles(ids)
                dao.deleteEquipment(ids)
                dao.upsertExercises(listOf(rows.exercise))
                dao.upsertExerciseTexts(listOf(rows.text))
                dao.upsertExerciseMuscles(rows.muscles)
                dao.upsertExerciseEquipment(rows.equipment)
            }
        }
    }
