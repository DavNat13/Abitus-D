package com.abitusd.app.feature.library.data

import com.abitusd.app.core.database.catalog.ExerciseEntity
import com.abitusd.app.core.database.catalog.ExerciseEquipmentEntity
import com.abitusd.app.core.database.catalog.ExerciseMuscleEntity
import com.abitusd.app.core.database.catalog.ExerciseTextEntity
import com.abitusd.app.core.i18n.SearchText
import com.abitusd.app.core.network.dto.OwnExerciseDto
import com.abitusd.app.core.network.dto.OwnMuscleDto
import java.time.Instant

/** Input of the own exercise form. Values are already validated by the form. */
data class OwnExerciseInput(
    val id: String,
    val name: String,
    val notes: String,
    val locale: String,
    val disciplineId: Int,
    val metricType: String,
    val movementPattern: String,
    val difficulty: Int,
    val primaryMuscles: Set<Int>,
    val secondaryMuscles: Set<Int>,
    val equipmentIds: Set<Int>,
    val defaultRestSeconds: Int,
)

/** Local rows of one own exercise and their conversion to and from the sync contract. */
data class OwnExerciseRows(
    val exercise: ExerciseEntity,
    val text: ExerciseTextEntity,
    val muscles: List<ExerciseMuscleEntity>,
    val equipment: List<ExerciseEquipmentEntity>,
)

object OwnExerciseMapper {
    private const val PRIMARY = 1.0
    private const val SECONDARY = 0.5

    fun rows(
        input: OwnExerciseInput,
        updatedAt: Long,
        dirty: Boolean,
        serverSeq: Long = 0,
    ): OwnExerciseRows {
        val exercise =
            ExerciseEntity(
                id = input.id,
                slug = null,
                isOwn = true,
                disciplineId = input.disciplineId,
                metricType = input.metricType,
                movementPattern = input.movementPattern,
                difficulty = input.difficulty,
                isUnilateral = false,
                usesBodyweight = input.metricType.startsWith("bodyweight"),
                defaultRestSeconds = input.defaultRestSeconds,
                source = null,
                license = null,
                updatedAt = updatedAt,
                deletedAt = null,
                dirty = dirty,
                serverSeq = serverSeq,
            )
        val text =
            ExerciseTextEntity(
                exerciseId = input.id,
                locale = input.locale,
                name = input.name.trim(),
                searchName = SearchText.normalize(input.name),
                summary = input.notes.trim(),
                setup = "",
                execution = emptyList(),
                cues = emptyList(),
                mistakes = emptyList(),
                safety = "",
            )
        val muscles =
            input.primaryMuscles.map { ExerciseMuscleEntity(input.id, it, "primary", PRIMARY) } +
                (input.secondaryMuscles - input.primaryMuscles).map {
                    ExerciseMuscleEntity(input.id, it, "secondary", SECONDARY)
                }
        return OwnExerciseRows(exercise, text, muscles, input.equipmentIds.map { ExerciseEquipmentEntity(input.id, it) })
    }

    fun toDto(rows: OwnExerciseRows): OwnExerciseDto =
        OwnExerciseDto(
            id = rows.exercise.id,
            disciplineId = rows.exercise.disciplineId,
            metricType = rows.exercise.metricType,
            movementPattern = rows.exercise.movementPattern,
            difficulty = rows.exercise.difficulty,
            isUnilateral = rows.exercise.isUnilateral,
            usesBodyweight = rows.exercise.usesBodyweight,
            defaultRestSeconds = rows.exercise.defaultRestSeconds,
            locale = rows.text.locale,
            name = rows.text.name,
            notes = rows.text.summary,
            muscles = rows.muscles.map { OwnMuscleDto(it.muscleGroupId, it.role) },
            equipmentIds = rows.equipment.map { it.equipmentId },
            updatedAt = Instant.ofEpochMilli(rows.exercise.updatedAt).toString(),
        )

    fun fromDto(dto: OwnExerciseDto): OwnExerciseInput =
        OwnExerciseInput(
            id = dto.id,
            name = dto.name,
            notes = dto.notes,
            locale = dto.locale,
            disciplineId = dto.disciplineId,
            metricType = dto.metricType,
            movementPattern = dto.movementPattern,
            difficulty = dto.difficulty,
            primaryMuscles = dto.muscles.filter { it.role == "primary" }.map { it.muscleGroupId }.toSet(),
            secondaryMuscles = dto.muscles.filter { it.role == "secondary" }.map { it.muscleGroupId }.toSet(),
            equipmentIds = dto.equipmentIds.toSet(),
            defaultRestSeconds = dto.defaultRestSeconds,
        )
}
