package com.abitusd.app.feature.library.domain

import com.abitusd.app.core.i18n.SearchText

/** Candidate exercise for duplicate detection. */
data class NamedExercise(
    val id: String,
    val name: String,
)

/**
 * Finds existing exercises whose name is the same or very close to a new name, ignoring case
 * and accents, so the user does not create a second copy of an exercise (10b, Fase 2).
 */
object DuplicateDetector {
    private const val MIN_LENGTH = 4
    private const val MAX_RESULTS = 3
    private const val SHORT_NAME = 8
    private const val TYPOS_SHORT = 1
    private const val TYPOS_LONG = 2

    fun similar(
        name: String,
        candidates: List<NamedExercise>,
        excludeId: String? = null,
    ): List<NamedExercise> {
        val target = SearchText.normalize(name)
        if (target.length < MIN_LENGTH) return emptyList()
        val allowed = if (target.length <= SHORT_NAME) TYPOS_SHORT else TYPOS_LONG
        return candidates
            .asSequence()
            .filter { it.id != excludeId }
            .map { it to SearchText.normalize(it.name) }
            .filter { (_, other) -> other == target || distance(other, target) <= allowed }
            .sortedBy { (_, other) -> distance(other, target) }
            .map { it.first }
            .distinctBy { it.id }
            .take(MAX_RESULTS)
            .toList()
    }

    /** Levenshtein distance with two rows of memory. */
    fun distance(
        a: String,
        b: String,
    ): Int {
        var previous = IntArray(b.length + 1) { it }
        var current = IntArray(b.length + 1)
        for (i in 1..a.length) {
            current[0] = i
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                current[j] = minOf(previous[j] + 1, current[j - 1] + 1, previous[j - 1] + cost)
            }
            val swap = previous
            previous = current
            current = swap
        }
        return previous[b.length]
    }
}
