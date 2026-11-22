package com.abitusd.app.feature.library.data

import com.abitusd.app.core.database.catalog.DisciplineEntity
import com.abitusd.app.core.database.catalog.DisciplineTextEntity
import com.abitusd.app.core.database.catalog.EquipmentEntity
import com.abitusd.app.core.database.catalog.EquipmentTextEntity
import com.abitusd.app.core.database.catalog.ExerciseEntity
import com.abitusd.app.core.database.catalog.ExerciseEquipmentEntity
import com.abitusd.app.core.database.catalog.ExerciseMuscleEntity
import com.abitusd.app.core.database.catalog.ExerciseRelationEntity
import com.abitusd.app.core.database.catalog.ExerciseTextEntity
import com.abitusd.app.core.database.catalog.MuscleGroupEntity
import com.abitusd.app.core.database.catalog.MuscleGroupTextEntity
import com.abitusd.app.core.i18n.SearchText
import com.abitusd.app.core.network.dto.CatalogDto
import com.abitusd.app.core.network.dto.CatalogExerciseDto

/** Room rows of one catalog download. Pure mapping, tested without a device. */
data class CatalogRows(
    val disciplines: List<DisciplineEntity>,
    val disciplineTexts: List<DisciplineTextEntity>,
    val muscles: List<MuscleGroupEntity>,
    val muscleTexts: List<MuscleGroupTextEntity>,
    val equipment: List<EquipmentEntity>,
    val equipmentTexts: List<EquipmentTextEntity>,
    val exercises: List<ExerciseEntity>,
    val texts: List<ExerciseTextEntity>,
    val exerciseMuscles: List<ExerciseMuscleEntity>,
    val exerciseEquipment: List<ExerciseEquipmentEntity>,
    val relations: List<ExerciseRelationEntity>,
)

object CatalogMapper {
    fun toRows(
        dto: CatalogDto,
        now: Long,
    ): CatalogRows =
        CatalogRows(
            disciplines = dto.disciplines.map { DisciplineEntity(it.id, it.slug, it.sortOrder, it.iconKey) },
            disciplineTexts =
                dto.disciplines.flatMap { d ->
                    d.names.map { (locale, name) ->
                        DisciplineTextEntity(d.id, locale, name, d.descriptions[locale].orEmpty())
                    }
                },
            muscles =
                dto.muscleGroups.map {
                    MuscleGroupEntity(it.id, it.slug, it.bodyRegion, it.view, it.svgKey, it.meshKey, it.sortOrder)
                },
            muscleTexts =
                dto.muscleGroups.flatMap { m -> m.names.map { (l, n) -> MuscleGroupTextEntity(m.id, l, n) } },
            equipment = dto.equipment.map { EquipmentEntity(it.id, it.slug) },
            equipmentTexts = dto.equipment.flatMap { q -> q.names.map { (l, n) -> EquipmentTextEntity(q.id, l, n) } },
            exercises = dto.exercises.map { exercise(it, now) },
            texts = dto.exercises.flatMap(::texts),
            exerciseMuscles =
                dto.exercises.flatMap { e ->
                    e.muscles.map { ExerciseMuscleEntity(e.id, it.muscleGroupId, it.role, it.contribution) }
                },
            exerciseEquipment = dto.exercises.flatMap { e -> e.equipmentIds.map { ExerciseEquipmentEntity(e.id, it) } },
            relations =
                dto.exercises.flatMap { e -> e.relations.map { ExerciseRelationEntity(e.id, it.exerciseId, it.type) } },
        )

    private fun exercise(
        e: CatalogExerciseDto,
        now: Long,
    ) = ExerciseEntity(
        id = e.id,
        slug = e.slug,
        isOwn = false,
        disciplineId = e.disciplineId,
        metricType = e.metricType,
        movementPattern = e.movementPattern,
        difficulty = e.difficulty,
        isUnilateral = e.isUnilateral,
        usesBodyweight = e.usesBodyweight,
        defaultRestSeconds = e.defaultRestSeconds,
        source = e.source,
        license = e.license,
        updatedAt = now,
        deletedAt = if (e.deleted) now else null,
        dirty = false,
        serverSeq = 0,
    )

    private fun texts(e: CatalogExerciseDto): List<ExerciseTextEntity> =
        e.translations.map { (locale, t) ->
            ExerciseTextEntity(
                exerciseId = e.id,
                locale = locale,
                name = t.name,
                searchName = SearchText.normalize(t.name),
                summary = t.summary,
                setup = t.setup,
                execution = t.execution,
                cues = t.cues,
                mistakes = t.commonMistakes,
                safety = t.safetyNotes,
            )
        }
}
