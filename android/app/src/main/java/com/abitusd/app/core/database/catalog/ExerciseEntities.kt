package com.abitusd.app.core.database.catalog

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

/**
 * Catalog and own exercises. Own exercises (isOwn) are written locally first and marked dirty
 * until the server accepts them (09b-FRONTEND-OFFLINE-SYNC.md). Times are epoch milliseconds.
 */
@Entity(tableName = "exercises", indices = [Index("disciplineId"), Index("dirty")])
data class ExerciseEntity(
    @PrimaryKey val id: String,
    val slug: String?,
    val isOwn: Boolean,
    val disciplineId: Int,
    val metricType: String,
    val movementPattern: String,
    val difficulty: Int,
    val isUnilateral: Boolean,
    val usesBodyweight: Boolean,
    val defaultRestSeconds: Int,
    val source: String?,
    val license: String?,
    val updatedAt: Long,
    val deletedAt: Long?,
    val dirty: Boolean,
    val serverSeq: Long,
)

/** Texts per locale. searchName is the name in lower case without accents, for search. */
@Entity(tableName = "exercise_texts", primaryKeys = ["exerciseId", "locale"], indices = [Index("searchName")])
data class ExerciseTextEntity(
    val exerciseId: String,
    val locale: String,
    val name: String,
    val searchName: String,
    val summary: String,
    val setup: String,
    val execution: List<String>,
    val cues: List<String>,
    val mistakes: List<String>,
    val safety: String,
)

@Entity(
    tableName = "exercise_muscles",
    primaryKeys = ["exerciseId", "muscleGroupId"],
    indices = [Index("muscleGroupId")],
)
data class ExerciseMuscleEntity(
    val exerciseId: String,
    val muscleGroupId: Int,
    val role: String,
    val contribution: Double,
)

@Entity(
    tableName = "exercise_equipment",
    primaryKeys = ["exerciseId", "equipmentId"],
    indices = [Index("equipmentId")],
)
data class ExerciseEquipmentEntity(
    val exerciseId: String,
    val equipmentId: Int,
)

@Entity(tableName = "exercise_relations", primaryKeys = ["exerciseId", "relatedId", "type"])
data class ExerciseRelationEntity(
    val exerciseId: String,
    val relatedId: String,
    val type: String,
)
