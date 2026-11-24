package com.abitusd.app.core.database.planning

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

/** Equipment slug of one exercise, for travel or place mode. */
data class ExerciseSlug(
    val exerciseId: String,
    val slug: String,
)

/** Blocks, substitutions and muscle targets; soft-deleted rows are hidden from the screens. */
@Dao
interface PeriodizationDao {
    @Upsert suspend fun upsertBlocks(rows: List<ProgramBlockEntity>)

    @Upsert suspend fun upsertSubstitutions(rows: List<BlockSubstitutionEntity>)

    @Upsert suspend fun upsertTargets(rows: List<MuscleTargetEntity>)

    @Query("SELECT * FROM program_blocks WHERE programId = :programId AND deletedAt IS NULL ORDER BY position")
    fun blocks(programId: String): Flow<List<ProgramBlockEntity>>

    @Query("SELECT * FROM program_blocks WHERE programId = :programId AND deletedAt IS NULL ORDER BY position")
    suspend fun blocksOnce(programId: String): List<ProgramBlockEntity>

    @Query("SELECT * FROM block_substitutions WHERE blockId = :blockId AND deletedAt IS NULL")
    suspend fun substitutionsOnce(blockId: String): List<BlockSubstitutionEntity>

    @Query("SELECT * FROM muscle_targets WHERE deletedAt IS NULL")
    fun targets(): Flow<List<MuscleTargetEntity>>

    @Query(
        "SELECT sessionRpe FROM workout_sessions WHERE endedAt IS NOT NULL AND deletedAt IS NULL " +
            "AND sessionRpe IS NOT NULL ORDER BY endedAt DESC LIMIT :limit",
    )
    fun recentSessionRpe(limit: Int): Flow<List<Double>>

    @Query(
        "SELECT ee.exerciseId AS exerciseId, q.slug AS slug FROM exercise_equipment ee " +
            "JOIN equipment q ON q.id = ee.equipmentId WHERE ee.exerciseId IN (:ids)",
    )
    suspend fun equipmentSlugs(ids: List<String>): List<ExerciseSlug>

    @Query("SELECT * FROM exercise_relations WHERE exerciseId IN (:ids) AND type IN ('alternative', 'regression')")
    suspend fun swapRelations(ids: List<String>): List<com.abitusd.app.core.database.catalog.ExerciseRelationEntity>

    @Query("SELECT * FROM program_blocks WHERE id = :id")
    suspend fun block(id: String): ProgramBlockEntity?

    @Query("SELECT * FROM block_substitutions WHERE id = :id")
    suspend fun substitution(id: String): BlockSubstitutionEntity?

    @Query("SELECT * FROM muscle_targets WHERE id = :id")
    suspend fun target(id: String): MuscleTargetEntity?

    @Query("SELECT * FROM program_blocks WHERE dirty = 1")
    suspend fun dirtyBlocks(): List<ProgramBlockEntity>

    @Query("SELECT * FROM block_substitutions WHERE dirty = 1")
    suspend fun dirtySubstitutions(): List<BlockSubstitutionEntity>

    @Query("SELECT * FROM muscle_targets WHERE dirty = 1")
    suspend fun dirtyTargets(): List<MuscleTargetEntity>

    @Query("UPDATE program_blocks SET dirty = 0 WHERE id = :id AND updatedAt = :at")
    suspend fun cleanBlock(
        id: String,
        at: Long,
    )

    @Query("UPDATE block_substitutions SET dirty = 0 WHERE id = :id AND updatedAt = :at")
    suspend fun cleanSubstitution(
        id: String,
        at: Long,
    )

    @Query("UPDATE muscle_targets SET dirty = 0 WHERE id = :id AND updatedAt = :at")
    suspend fun cleanTarget(
        id: String,
        at: Long,
    )

    @Query("DELETE FROM program_blocks")
    suspend fun clearBlocks()

    @Query("DELETE FROM block_substitutions")
    suspend fun clearSubstitutions()

    @Query("DELETE FROM muscle_targets")
    suspend fun clearTargets()
}
