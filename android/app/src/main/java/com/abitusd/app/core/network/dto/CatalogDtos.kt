package com.abitusd.app.core.network.dto

import kotlinx.serialization.Serializable

/** Wire contract of GET /v1/catalog (07-BACKEND.md). */
@Serializable
data class CatalogDto(
    val version: Int,
    val since: Int,
    val disciplines: List<DisciplineDto>,
    val muscleGroups: List<MuscleGroupDto>,
    val equipment: List<EquipmentDto>,
    val exercises: List<CatalogExerciseDto>,
)

@Serializable
data class DisciplineDto(
    val id: Int,
    val slug: String,
    val sortOrder: Int,
    val iconKey: String,
    val names: Map<String, String>,
    val descriptions: Map<String, String>,
)

@Serializable
data class MuscleGroupDto(
    val id: Int,
    val slug: String,
    val bodyRegion: String,
    val view: String,
    val meshKey: String,
    val svgKey: String,
    val sortOrder: Int,
    val names: Map<String, String>,
)

@Serializable
data class EquipmentDto(
    val id: Int,
    val slug: String,
    val names: Map<String, String>,
)

@Serializable
data class ExerciseTextsDto(
    val name: String,
    val summary: String = "",
    val setup: String = "",
    val execution: List<String> = emptyList(),
    val cues: List<String> = emptyList(),
    val commonMistakes: List<String> = emptyList(),
    val safetyNotes: String = "",
)

@Serializable
data class ExerciseMuscleDto(
    val muscleGroupId: Int,
    val role: String,
    val contribution: Double = 1.0,
)

@Serializable
data class ExerciseRelationDto(
    val exerciseId: String,
    val type: String,
)

@Serializable
data class CatalogExerciseDto(
    val id: String,
    val slug: String? = null,
    val disciplineId: Int,
    val metricType: String,
    val movementPattern: String,
    val difficulty: Int,
    val isUnilateral: Boolean,
    val usesBodyweight: Boolean,
    val defaultRestSeconds: Int,
    val source: String? = null,
    val license: String? = null,
    val deleted: Boolean,
    val translations: Map<String, ExerciseTextsDto>,
    val muscles: List<ExerciseMuscleDto>,
    val equipmentIds: List<Int>,
    val relations: List<ExerciseRelationDto>,
)
