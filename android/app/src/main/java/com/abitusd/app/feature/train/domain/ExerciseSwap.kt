package com.abitusd.app.feature.train.domain

/** An exercise as the swap needs it: the equipment it requires. */
data class SwapCandidate(
    val id: String,
    val equipment: Set<String>,
)

/**
 * Block substitutions and travel or place mode (10f Fase 4b). A block substitution always wins;
 * then, with a place profile, an exercise whose equipment is missing becomes its first related
 * exercise (alternative or regression, in the given order) that fits the profile.
 */
object ExerciseSwap {
    fun resolve(
        planned: List<String>,
        blockSubstitutions: Map<String, String>,
        catalog: Map<String, SwapCandidate>,
        related: Map<String, List<String>>,
        available: Set<String>?,
    ): Map<String, String> {
        val out = mutableMapOf<String, String>()
        for (id in planned) {
            val chosen = blockSubstitutions[id] ?: id
            val swapped =
                if (available == null || fits(catalog[chosen], available)) {
                    chosen
                } else {
                    related[chosen].orEmpty().firstOrNull { fits(catalog[it], available) } ?: chosen
                }
            if (swapped != id) out[id] = swapped
        }
        return out
    }

    private fun fits(
        candidate: SwapCandidate?,
        available: Set<String>,
    ): Boolean = candidate != null && available.containsAll(candidate.equipment)
}
