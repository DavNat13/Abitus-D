package com.abitusd.app.feature.library.data

import com.abitusd.app.core.network.dto.CatalogDto
import com.abitusd.app.core.network.dto.CatalogExerciseDto
import com.abitusd.app.core.network.dto.DisciplineDto
import com.abitusd.app.core.network.dto.ExerciseMuscleDto
import com.abitusd.app.core.network.dto.ExerciseRelationDto
import com.abitusd.app.core.network.dto.ExerciseTextsDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CatalogMapperTest {
    private fun exercise(
        id: String,
        deleted: Boolean = false,
    ) = CatalogExerciseDto(
        id = id,
        slug = "push_up",
        disciplineId = 1,
        metricType = "bodyweight_reps",
        movementPattern = "push",
        difficulty = 2,
        isUnilateral = false,
        usesBodyweight = true,
        defaultRestSeconds = 90,
        source = "free-exercise-db",
        license = "Unlicense",
        deleted = deleted,
        translations =
            mapOf(
                "es" to ExerciseTextsDto(name = "Flexión de brazos", execution = listOf("Baja", "Sube")),
                "en" to ExerciseTextsDto(name = "Push-Up"),
            ),
        muscles = listOf(ExerciseMuscleDto(1, "primary", 1.0), ExerciseMuscleDto(17, "secondary", 0.5)),
        equipmentIds = listOf(1),
        relations = listOf(ExerciseRelationDto("other", "progression")),
    )

    private val dto =
        CatalogDto(
            version = 6,
            since = 0,
            disciplines = listOf(DisciplineDto(1, "calisthenics", 1, "icon", mapOf("es" to "Calistenia"), mapOf())),
            muscleGroups = emptyList(),
            equipment = emptyList(),
            exercises = listOf(exercise("a"), exercise("b", deleted = true)),
        )

    @Test
    fun mapsEveryLocaleWithSearchableNames() {
        val rows = CatalogMapper.toRows(dto, now = 1000)
        val spanish = rows.texts.first { it.exerciseId == "a" && it.locale == "es" }
        assertEquals("flexion de brazos", spanish.searchName)
        assertEquals(listOf("Baja", "Sube"), spanish.execution)
        assertEquals(4, rows.texts.size)
    }

    @Test
    fun keepsMusclesEquipmentAndRelations() {
        val rows = CatalogMapper.toRows(dto, now = 1000)
        assertEquals(4, rows.exerciseMuscles.size)
        assertEquals(0.5, rows.exerciseMuscles.first { it.role == "secondary" }.contribution, 0.0)
        assertEquals(2, rows.exerciseEquipment.size)
        assertEquals("progression", rows.relations.first().type)
        assertEquals("", rows.disciplineTexts.single().description)
    }

    @Test
    fun deletedCatalogExercisesBecomeTombstones() {
        val rows = CatalogMapper.toRows(dto, now = 1000)
        assertNull(rows.exercises.first { it.id == "a" }.deletedAt)
        assertNotNull(rows.exercises.first { it.id == "b" }.deletedAt)
    }
}
