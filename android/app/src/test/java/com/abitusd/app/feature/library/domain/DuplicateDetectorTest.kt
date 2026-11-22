package com.abitusd.app.feature.library.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DuplicateDetectorTest {
    private val catalog =
        listOf(
            NamedExercise("1", "Flexión de brazos"),
            NamedExercise("2", "Dominada"),
            NamedExercise("3", "Press de banca con barra"),
        )

    @Test
    fun ignoresCaseAndAccents() {
        assertEquals(listOf("1"), DuplicateDetector.similar("flexion de brazos", catalog).map { it.id })
    }

    @Test
    fun toleratesSmallTypos() {
        assertEquals(listOf("3"), DuplicateDetector.similar("Pres de banca con barra", catalog).map { it.id })
        assertEquals(listOf("2"), DuplicateDetector.similar("Dominda", catalog).map { it.id })
    }

    @Test
    fun differentNamesAreNotDuplicates() {
        assertTrue(DuplicateDetector.similar("Sentadilla búlgara", catalog).isEmpty())
        assertTrue(DuplicateDetector.similar("Pre", catalog).isEmpty())
    }

    @Test
    fun excludesTheExerciseBeingEdited() {
        assertTrue(DuplicateDetector.similar("Dominada", catalog, excludeId = "2").isEmpty())
    }

    @Test
    fun levenshteinDistance() {
        assertEquals(3, DuplicateDetector.distance("kitten", "sitting"))
        assertEquals(0, DuplicateDetector.distance("remo", "remo"))
    }
}
