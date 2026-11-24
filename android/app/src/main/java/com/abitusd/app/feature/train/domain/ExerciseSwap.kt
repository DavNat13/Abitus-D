package com.abitusd.app.feature.train.domain

/** An exercise as the swap needs it: the equipment it requires. */
data class SwapCandidate(
    val id: String,
    val equipment: Set<String>,
)

/**
 * Block substitutions and travel or place mode (10f Fase 4b). A block substitution always wins;
 * then, with a place profile, an exercise whose equipment is missing becomes its first related
 * exercise (alternative or regression, in the given order) that fits the profile. With
 * [excludes] the profile lists missing equipment instead of available equipment.
 */
object ExerciseSwap {
    fun resolve(
        planned: List<String>,
        blockSubstitutions: Map<String, String>,
        catalog: Map<String, SwapCandidate>,
        related: Map<String, List<String>>,
        available: Set<String>?,
        excludes: Boolean = false,
    ): Map<String, String> {
        val out = mutableMapOf<String, String>()
        for (id in planned) {
            val chosen = blockSubstitutions[id] ?: id
            val swapped =
                if (available == null || fits(catalog[chosen], available, excludes)) {
                    chosen
                } else {
                    related[chosen].orEmpty().firstOrNull { fits(catalog[it], available, excludes) } ?: chosen
                }
            if (swapped != id) out[id] = swapped
        }
        return out
    }

    fun fits(
        candidate: SwapCandidate?,
        available: Set<String>,
        excludes: Boolean,
    ): Boolean =
        candidate != null &&
            if (excludes) candidate.equipment.none { it in available } else available.containsAll(candidate.equipment)
}
