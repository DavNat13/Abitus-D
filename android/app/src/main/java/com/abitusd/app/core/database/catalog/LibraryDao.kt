package com.abitusd.app.core.database.catalog

import androidx.room3.Dao
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

/** Row of the library list with the name already resolved for the locale. */
data class ExerciseListRow(
    val id: String,
    val isOwn: Boolean,
    val disciplineId: Int,
    val difficulty: Int,
    val name: String,
    val searchName: String,
)

/** Catalog id of a slug. */
data class SlugId(
    val id: String,
    val slug: String,
)

data class NamedRef(
    val id: Int,
    val name: String,
)

/**
 * Library reads. Names follow the fallback chain of 09: chosen locale, English, Spanish, slug.
 * Every filter is optional (null means no filter).
 */
@Dao
interface LibraryDao {
    @Query(
        """
        SELECT e.id, e.isOwn, e.disciplineId, e.difficulty,
            COALESCE(t1.name, t2.name, t3.name, e.slug, '') AS name,
            COALESCE(t1.searchName, t2.searchName, t3.searchName, '') AS searchName
        FROM exercises e
        LEFT JOIN exercise_texts t1 ON t1.exerciseId = e.id AND t1.locale = :locale
        LEFT JOIN exercise_texts t2 ON t2.exerciseId = e.id AND t2.locale = 'en'
        LEFT JOIN exercise_texts t3 ON t3.exerciseId = e.id AND t3.locale = 'es'
        WHERE e.deletedAt IS NULL
          AND (:query = '' OR t1.searchName LIKE '%' || :query || '%'
               OR t2.searchName LIKE '%' || :query || '%' OR t3.searchName LIKE '%' || :query || '%')
          AND (:disciplineId IS NULL OR e.disciplineId = :disciplineId)
          AND (:difficulty IS NULL OR e.difficulty = :difficulty)
          AND (:muscleId IS NULL OR EXISTS (SELECT 1 FROM exercise_muscles m
               WHERE m.exerciseId = e.id AND m.muscleGroupId = :muscleId))
          AND (:equipmentId IS NULL OR EXISTS (SELECT 1 FROM exercise_equipment q
               WHERE q.exerciseId = e.id AND q.equipmentId = :equipmentId))
          AND (:profile = 0 OR NOT EXISTS (SELECT 1 FROM exercise_equipment p JOIN equipment k ON k.id = p.equipmentId
               WHERE p.exerciseId = e.id AND ((:profile = 1 AND k.slug IN (:slugs)) OR (:profile = 2 AND k.slug NOT IN (:slugs)))))
        ORDER BY name COLLATE NOCASE
        """,
    )
    fun exercises(
        locale: String,
        query: String,
        disciplineId: Int?,
        muscleId: Int?,
        equipmentId: Int?,
        difficulty: Int?,
        profile: Int = 0,
        slugs: List<String> = emptyList(),
    ): Flow<List<ExerciseListRow>>

    @Query(
        """
        SELECT d.id, COALESCE(t1.name, t2.name, d.slug) AS name FROM disciplines d
        LEFT JOIN discipline_texts t1 ON t1.disciplineId = d.id AND t1.locale = :locale
        LEFT JOIN discipline_texts t2 ON t2.disciplineId = d.id AND t2.locale = 'en'
        ORDER BY d.sortOrder
        """,
    )
    fun disciplines(locale: String): Flow<List<NamedRef>>

    @Query(
        """
        SELECT g.id, COALESCE(t1.name, t2.name, g.slug) AS name FROM muscle_groups g
        LEFT JOIN muscle_group_texts t1 ON t1.muscleGroupId = g.id AND t1.locale = :locale
        LEFT JOIN muscle_group_texts t2 ON t2.muscleGroupId = g.id AND t2.locale = 'en'
        ORDER BY g.sortOrder
        """,
    )
    fun muscleGroups(locale: String): Flow<List<NamedRef>>

    @Query(
        """
        SELECT q.id, COALESCE(t1.name, t2.name, q.slug) AS name FROM equipment q
        LEFT JOIN equipment_texts t1 ON t1.equipmentId = q.id AND t1.locale = :locale
        LEFT JOIN equipment_texts t2 ON t2.equipmentId = q.id AND t2.locale = 'en'
        ORDER BY name COLLATE NOCASE
        """,
    )
    fun equipment(locale: String): Flow<List<NamedRef>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun exercise(id: String): Flow<ExerciseEntity?>

    @Query("SELECT * FROM exercise_texts WHERE exerciseId = :id")
    fun texts(id: String): Flow<List<ExerciseTextEntity>>

    @Query("SELECT * FROM exercise_muscles WHERE exerciseId = :id ORDER BY role, muscleGroupId")
    fun muscles(id: String): Flow<List<ExerciseMuscleEntity>>

    @Query("SELECT * FROM exercise_equipment WHERE exerciseId = :id")
    fun equipmentOf(id: String): Flow<List<ExerciseEquipmentEntity>>

    @Query("SELECT * FROM exercise_relations WHERE exerciseId = :id")
    fun relations(id: String): Flow<List<ExerciseRelationEntity>>

    @Query("SELECT id, slug FROM exercises WHERE slug IN (:slugs) AND deletedAt IS NULL")
    suspend fun idsBySlug(slugs: List<String>): List<SlugId>

    @Query("SELECT * FROM exercise_muscles WHERE exerciseId IN (:ids)")
    suspend fun musclesOf(ids: List<String>): List<ExerciseMuscleEntity>

    @Query("SELECT COUNT(*) FROM exercises WHERE isOwn = 0")
    fun catalogCount(): Flow<Int>
}
