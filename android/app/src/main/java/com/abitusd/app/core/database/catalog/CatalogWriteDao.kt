package com.abitusd.app.core.database.catalog

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert

/** Writes coming from the server catalog and from own exercises. */
@Dao
interface CatalogWriteDao {
    @Upsert suspend fun upsertDisciplines(rows: List<DisciplineEntity>)

    @Upsert suspend fun upsertDisciplineTexts(rows: List<DisciplineTextEntity>)

    @Upsert suspend fun upsertMuscleGroups(rows: List<MuscleGroupEntity>)

    @Upsert suspend fun upsertMuscleGroupTexts(rows: List<MuscleGroupTextEntity>)

    @Upsert suspend fun upsertEquipment(rows: List<EquipmentEntity>)

    @Upsert suspend fun upsertEquipmentTexts(rows: List<EquipmentTextEntity>)

    @Upsert suspend fun upsertExercises(rows: List<ExerciseEntity>)

    @Upsert suspend fun upsertExerciseTexts(rows: List<ExerciseTextEntity>)

    @Upsert suspend fun upsertExerciseMuscles(rows: List<ExerciseMuscleEntity>)

    @Upsert suspend fun upsertExerciseEquipment(rows: List<ExerciseEquipmentEntity>)

    @Upsert suspend fun upsertExerciseRelations(rows: List<ExerciseRelationEntity>)

    @Query("DELETE FROM exercise_texts WHERE exerciseId IN (:ids)")
    suspend fun deleteTexts(ids: List<String>)

    @Query("DELETE FROM exercise_muscles WHERE exerciseId IN (:ids)")
    suspend fun deleteMuscles(ids: List<String>)

    @Query("DELETE FROM exercise_equipment WHERE exerciseId IN (:ids)")
    suspend fun deleteEquipment(ids: List<String>)

    @Query("DELETE FROM exercise_relations WHERE exerciseId IN (:ids)")
    suspend fun deleteRelations(ids: List<String>)

    @Query("SELECT * FROM exercises WHERE isOwn = 1 AND dirty = 1")
    suspend fun dirtyOwnExercises(): List<ExerciseEntity>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun exercise(id: String): ExerciseEntity?

    @Query("SELECT * FROM exercise_texts WHERE exerciseId = :id")
    suspend fun texts(id: String): List<ExerciseTextEntity>

    @Query("SELECT * FROM exercise_muscles WHERE exerciseId = :id")
    suspend fun muscles(id: String): List<ExerciseMuscleEntity>

    @Query("SELECT * FROM exercise_equipment WHERE exerciseId = :id")
    suspend fun equipment(id: String): List<ExerciseEquipmentEntity>

    @Query("UPDATE exercises SET dirty = 0 WHERE id = :id AND updatedAt = :updatedAt")
    suspend fun markClean(
        id: String,
        updatedAt: Long,
    )

    @Query("DELETE FROM exercises WHERE isOwn = 1")
    suspend fun clearOwnExercises()
}
